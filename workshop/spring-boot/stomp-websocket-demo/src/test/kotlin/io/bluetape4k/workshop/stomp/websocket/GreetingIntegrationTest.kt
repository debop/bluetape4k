package io.bluetape4k.workshop.stomp.websocket

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.json.jackson.Jackson
import io.bluetape4k.junit5.awaitility.untilSuspending
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.stomp.websocket.model.Greeting
import io.bluetape4k.workshop.stomp.websocket.model.HelloMessage
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.future.await
import org.amshove.kluent.shouldBeEqualTo
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingIntegrationTest(
    @LocalServerPort private val port: Int,
) {
    companion object: KLogging()

    val wsUrl by lazy { "ws://localhost:$port/gs-guide-websocket" }

    private lateinit var stompClient: WebSocketStompClient
    private val headers = WebSocketHttpHeaders()

    @BeforeEach
    fun beforeEach() {
        val transports = fastListOf<Transport>(WebSocketTransport(StandardWebSocketClient()))
        val socketJsClient = SockJsClient(transports)
        this.stompClient = WebSocketStompClient(socketJsClient).apply {
            messageConverter = MappingJackson2MessageConverter().apply {
                objectMapper = Jackson.defaultJsonMapper
            }
        }
        log.debug { "Local server port: $port" }
    }

    @Test
    fun `get greeting`() {
        val received = atomic<Greeting?>(null)
        val failure = atomic<Throwable?>(null)
        val handler: StompSessionHandler = getStopmSessionHandler(received, failure)

        val session = stompClient.connectAsync(wsUrl, headers, handler, port).get()

        log.debug { "Send HelloMessage to /app/hello" }
        try {
            session.send("/app/hello", HelloMessage("Spring"))
        } catch (e: Throwable) {
            failure.value = e
        }

        await until { received.value != null || failure.value != null }

        if (failure.value == null) {
            received.value!!.content shouldBeEqualTo "Hello, Spring!"
        } else {
            fail(failure.value)
        }
    }

    @Test
    fun `get greeting with coroutines`() = runSuspendWithIO {
        val received = atomic<Greeting?>(null)
        val failure = atomic<Throwable?>(null)
        val handler: StompSessionHandler = getStopmSessionHandler(received, failure)

        val session = stompClient.connectAsync(wsUrl, headers, handler, port).await()

        log.debug { "Send HelloMessage to /app/hello" }
        try {
            session.send("/app/hello", HelloMessage("Spring"))
        } catch (e: Throwable) {
            failure.value = e
        }

        await untilSuspending { received.value != null || failure.value != null }

        if (failure.value == null) {
            received.value!!.content shouldBeEqualTo "Hello, Spring!"
        } else {
            fail(failure.value)
        }
    }

    private fun getStopmSessionHandler(
        received: AtomicRef<Greeting?>,
        failure: AtomicRef<Throwable?>,
    ): StompSessionHandler = object: TestSessionHandler(failure) {
        override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
            log.debug { "Stomp session connected. subscribe /topic/greetings" }
            session.subscribe(
                "/topic/greetings",
                object: StompFrameHandler {
                    override fun getPayloadType(headers: StompHeaders): Type = Greeting::class.java

                    override fun handleFrame(headers: StompHeaders, payload: Any?) {
                        log.debug { "Payload: $payload" }
                        try {
                            val greeting = payload as Greeting
                            received.value = greeting
                            log.debug { "Receive: $greeting" }
                        } catch (e: Throwable) {
                            failure.value = e
                        } finally {
                            session.disconnect()
                        }
                    }
                }
            )
        }
    }
}
