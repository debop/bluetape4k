package io.bluetape4k.workshop.cloud.gateway.routes

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.local.SynchronizationStrategy
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Duration


class ThrottleGatewayFilter: GatewayFilter {

    companion object: KLogging()

    // TODO: 이건 Local 이다. 향후 LettuceBasedProxyManager 를 이용한 BucketProxy 를 이용하여 분산 방식을 사용할 수 있다
    private val bucket: Bucket = Bucket.builder()
        .withMillisecondPrecision()
        .withSynchronizationStrategy(SynchronizationStrategy.LOCK_FREE)
        .addLimit(Bandwidth.builder().capacity(1).refillIntervally(1, Duration.ofSeconds(10)).build())
        .build()

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        log.debug { "Bucket4j token-bucket availableTokens: ${bucket.availableTokens}" }
        val consumed = bucket.tryConsume(1L)

        return if (consumed) {
            chain.filter(exchange)
        } else {
            exchange.response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
            exchange.response.setComplete()
        }
    }
}
