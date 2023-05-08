package io.bluetape4k.workshop.stomp.websocket

import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import java.util.concurrent.atomic.AtomicReference

open class TestSessionHandler(
    private val failure: AtomicReference<Throwable>,
): StompSessionHandlerAdapter() {

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        this.failure.set(RuntimeException(headers.toString()))
    }

    override fun handleException(
        session: StompSession,
        command: StompCommand?,
        headers: StompHeaders,
        payload: ByteArray,
        exception: Throwable,
    ) {
        this.failure.set(exception)
    }

    override fun handleTransportError(session: StompSession, exception: Throwable) {
        this.failure.set(exception)
    }
}
