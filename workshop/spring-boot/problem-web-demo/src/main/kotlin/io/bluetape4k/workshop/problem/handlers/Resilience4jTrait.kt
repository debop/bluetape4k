package io.bluetape4k.workshop.problem.handlers

import io.github.resilience4j.bulkhead.BulkheadFullException
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import io.github.resilience4j.retry.MaxRetriesExceededException
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ServerWebExchange
import org.zalando.problem.Problem
import org.zalando.problem.Status
import org.zalando.problem.spring.webflux.advice.AdviceTrait
import reactor.core.publisher.Mono

/**
 * Resilience4j 관련 예외를 Problem 예외 메시지로 변환하는 [AdviceTrait]의 구현체입니다.
 */
interface Resilience4jTrait: AdviceTrait {

    @ExceptionHandler
    fun handleBulkheadFull(
        ex: BulkheadFullException,
        request: ServerWebExchange,
    ): Mono<ResponseEntity<Problem>> {
        return create(Status.TOO_MANY_REQUESTS, ex, request)
    }

    @ExceptionHandler
    fun handleCircuitBreakerCallNotPermitted(
        ex: CallNotPermittedException,
        request: ServerWebExchange,
    ): Mono<ResponseEntity<Problem>> {
        val headers = HttpHeaders().apply {
            add(HttpHeaders.RETRY_AFTER, "10")
        }
        return create(Status.SERVICE_UNAVAILABLE, ex, request, headers)
    }

    @ExceptionHandler
    fun handleRatelimiterRequestNotPermitted(
        ex: RequestNotPermitted,
        request: ServerWebExchange,
    ): Mono<ResponseEntity<Problem>> {
        return create(Status.BANDWIDTH_LIMIT_EXCEEDED, ex, request)
    }

    @ExceptionHandler
    fun handleRetryMaxRetriesExceeded(
        ex: MaxRetriesExceededException,
        request: ServerWebExchange,
    ): Mono<ResponseEntity<Problem>> {
        val headers = HttpHeaders().apply {
            add(HttpHeaders.RETRY_AFTER, "10 s")
        }
        return create(Status.INTERNAL_SERVER_ERROR, ex, request, headers)
    }
}
