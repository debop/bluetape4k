package io.bluetape4k.workshop.webflux.handler

import io.bluetape4k.workshop.webflux.service.QuoteGenerator
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class ReactiveWebSocketHandler(
    private val quoteGenerator: QuoteGenerator,
): WebSocketHandler {

    /**
     * Invoked when a new WebSocket connection is established, and allows
     * handling of the session.
     *
     *
     * See the class-level doc and the reference manual for more details and
     * examples of how to handle the session.
     *
     * @param session the session to handle
     * @return indicates when application handling of the session is complete,
     * which should reflect the completions of the inbound message stream
     * (i.e. connection closing) and possibly the completions of the outbound
     * message stream and the writing of messages
     */
    override fun handle(session: WebSocketSession): Mono<Void> {
        val flux = quoteGenerator
            .fetchQuoteStringAsFlux(Duration.ofSeconds(2))
            .map { quoteStr -> session.textMessage(quoteStr) }

        return session.send(flux)
            .and(session.receive().map { it.payloadAsText }.log())
    }
}
