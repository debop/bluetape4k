package io.bluetape4k.workshop.es.webfilter

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class RedirectWebFilter: WebFilter {

    companion object: KLogging() {
        private const val SWAGGER_PATH = "/swagger-ui.html"
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (exchange.request.uri.path == "/") {
            log.debug { "Redirect to $SWAGGER_PATH" }
            // Redirect to swagger page
            val swaggerRequest = exchange.request.mutate().path(SWAGGER_PATH).build()
            return chain.filter(exchange.mutate().request(swaggerRequest).build())
        }
        return chain.filter(exchange)
    }
}
