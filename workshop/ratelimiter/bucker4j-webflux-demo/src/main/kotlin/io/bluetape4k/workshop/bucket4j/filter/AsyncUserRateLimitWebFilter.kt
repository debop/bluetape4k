package io.bluetape4k.workshop.bucket4j.filter

import io.bluetape4k.bucket4j.ratelimit.distributed.DistributedCoRateLimiter
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.logging.warn
import io.bluetape4k.workshop.bucket4j.components.RateLimitTargetProvider
import io.bluetape4k.workshop.bucket4j.components.UserKeyResolver
import io.bluetape4k.workshop.bucket4j.utils.HeaderUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(1)
class AsyncUserRateLimitWebFilter(
    private val keyResolver: UserKeyResolver,
    private val rateLimiter: DistributedCoRateLimiter,
    private val targetProvider: RateLimitTargetProvider,
): WebFilter {

    companion object: KLogging()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return doFilter(exchange, chain)
    }

    protected fun doFilter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> = mono(Dispatchers.IO) {
        try {
            val targetPath = exchange.request.uri.path
            log.trace { "rate limit filtering request. path=$targetPath" }
            if (canFilter(exchange)) {
                val key = extractKey(exchange)
                log.trace { "Extracted key=$this" }

                if (!key.isNullOrBlank()) {
                    val result = rateLimiter.coConsume(key, 1L)
                    writeRateLimitHeaders(exchange, result.availableTokens)
                    if (result.consumedTokens > 0L) {
                        log.trace { "Bucket[$key] remains token=${result.availableTokens}" }
                        chain.filter(exchange).awaitSingleOrNull()
                    } else {
                        sendErrorResponse(exchange)
                        Mono.empty<Void>().awaitSingleOrNull()
                    }
                } else {
                    sendErrorResponse(exchange, HttpStatus.BAD_REQUEST)
                    Mono.empty<Void>().awaitSingleOrNull()
                }
            } else {
                log.trace { "Filtering request 가 아니므로 Rate Limit을 적용하지 않습니다. path=$targetPath" }
                chain.filter(exchange).awaitSingleOrNull()
            }
        } catch (e: Throwable) {
            log.warn(e) { "Rate Limit 적용에 실패했습니다. 무시하고, 다음 필터를 진행합니다." }
            chain.filter(exchange).awaitSingleOrNull()
        }
    }

    protected fun canFilter(exchange: ServerWebExchange): Boolean {
        return targetProvider.getTargets().any { it.matches(exchange.request.uri.path) }
    }

    protected fun extractKey(exchange: ServerWebExchange): String? {
        return keyResolver.resolve(exchange)
    }

    protected suspend fun writeRateLimitHeaders(exchange: ServerWebExchange, availableTokens: Long) {
        exchange.response.headers.set(HeaderUtils.X_BLUETAPE4K_REMAINING_TOKEN, availableTokens.toString())
    }

    protected fun sendErrorResponse(
        exchange: ServerWebExchange, status: HttpStatus = HttpStatus.TOO_MANY_REQUESTS,
    ) {
        log.warn { "Sending error response with status=$status" }
        exchange.response.statusCode = status // exchange.response.headers.contentType = MediaType.APPLICATION_JSON
    }
}
