package io.bluetape4k.workshop.stomp.websocket

import kotlinx.atomicfu.AtomicRef
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter

open class TestSessionHandler(
    private val failure: AtomicRef<Throwable?>,
): StompSessionHandlerAdapter() {

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        this.failure.value = RuntimeException(headers.toString())
    }

    override fun handleException(
        session: StompSession,
        command: StompCommand?,
        headers: StompHeaders,
        payload: ByteArray,
        exception: Throwable,
    ) {
        this.failure.value = exception
    }

    override fun handleTransportError(session: StompSession, exception: Throwable) {
        this.failure.value = exception
    }
}
