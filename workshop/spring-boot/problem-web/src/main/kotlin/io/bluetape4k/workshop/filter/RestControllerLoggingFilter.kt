package io.bluetape4k.workshop.filter

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * [ServerWebExchange]로부터 path 와 query parameters 를 추출하여 로깅하는 [WebFilter] 구현체입니다.
 */
class RestControllerLoggingFilter: WebFilter {

    companion object: KLogging()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        log.debug { "Web exchange. request path=${exchange.request.path}, query params=${exchange.request.queryParams}" }
        return chain.filter(exchange)
    }
}
