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

    override fun handle(session: WebSocketSession): Mono<Void> {
        val flux = quoteGenerator.fetchQuoteStringAsFlux(Duration.ofSeconds(2))
            .map { session.textMessage(it) }

        return session.send(flux)
            .and(session.receive().map { it.payloadAsText }.log())
    }
}
