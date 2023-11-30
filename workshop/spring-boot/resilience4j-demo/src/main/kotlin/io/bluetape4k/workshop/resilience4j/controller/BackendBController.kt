package io.bluetape4k.workshop.resilience4j.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.resilience4j.service.Service
import io.github.resilience4j.bulkhead.BulkheadFullException
import io.github.resilience4j.bulkhead.BulkheadRegistry
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.decorators.Decorators
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import io.github.resilience4j.reactor.retry.RetryOperator
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator
import io.github.resilience4j.retry.RetryRegistry
import io.github.resilience4j.timelimiter.TimeLimiterRegistry
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeoutException

@RestController
@RequestMapping("/backendB")
class BackendBController(
    @Qualifier("backendBService") private val businessBService: Service,
    circuitBreakerRegistry: CircuitBreakerRegistry,
    threadPoolBulkheadRegistry: ThreadPoolBulkheadRegistry,
    bulkheadRegistry: BulkheadRegistry,
    retryRegistry: RetryRegistry,
    rateLimiterRegistry: RateLimiterRegistry,
    timeLimiterRegistry: TimeLimiterRegistry
) {
    companion object: KLogging() {
        private const val BACKEND_B = "backendB"
    }

    private val circuitBreaker = circuitBreakerRegistry.circuitBreaker(BACKEND_B)
    private val bulkhead = bulkheadRegistry.bulkhead(BACKEND_B)
    private val threadPoolBulkhead = threadPoolBulkheadRegistry.bulkhead(BACKEND_B)
    private val retry = retryRegistry.retry(BACKEND_B)
    private val rateLimiter = rateLimiterRegistry.rateLimiter(BACKEND_B)
    private val timeLimiter = timeLimiterRegistry.timeLimiter(BACKEND_B)

    private val scheduledExecutorService = Executors.newScheduledThreadPool(3)

    @GetMapping("failure")
    fun failure() = execute { businessBService.failure() }

    @GetMapping("success")
    fun success() = execute { businessBService.success() }

    @GetMapping("successException")
    fun successException() = execute { businessBService.successException() }

    @GetMapping("ignore")
    fun ignore(): String = Decorators
        .ofSupplier { businessBService.ignoreException() }
        .withCircuitBreaker(circuitBreaker)
        .withBulkhead(bulkhead)
        .get()

    @GetMapping("monoSuccess")
    fun monoSuccess() = execute(businessBService.monoSuccess())

    @GetMapping("monoFailure")
    fun monoFailure() = execute(businessBService.monoFailure())

    @GetMapping("fluxSuccess")
    fun fluxSuccess() = execute(businessBService.fluxSuccess())

    @GetMapping("monoTimeout")
    fun monoTimeout() = executeWithFallback(businessBService.monoTimeout()) { monoFallback(it) }

    @GetMapping("fluxTimeout")
    fun fluxTimeout() = executeWithFallback(businessBService.fluxTimeout()) { fluxFallback(it) }

    @GetMapping("futureFailure")
    fun futureFailure() = executeAsync { businessBService.futureFailure() }

    @GetMapping("futureSuccess")
    fun futureSuccess() = executeAsync { businessBService.futureSuccess() }

    @GetMapping("futureTimeout")
    fun futureTimeout() = executeAsync { businessBService.futureTimeout() }

    @GetMapping("fluxFailure")
    fun fluxFailure() = execute(businessBService.fluxFailure())

    @GetMapping("fallback")
    fun failureWithFallback() = businessBService.failureWithFallback()

    private fun timeout(): String {
        try {
            Thread.sleep(10_000L)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun <T> execute(publisher: Mono<T>): Mono<T> {
        return publisher
            .transform(BulkheadOperator.of(bulkhead))
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .transform(RetryOperator.of(retry))
    }

    private fun <T> execute(publisher: Flux<T>): Flux<T> {
        return publisher
            .transform(BulkheadOperator.of(bulkhead))
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .transform(RetryOperator.of(retry))
    }

    private fun <T> executeWithFallback(publisher: Mono<T>, fallback: (Throwable) -> Mono<T>): Mono<T> {
        return publisher
            .transform(TimeLimiterOperator.of(timeLimiter))
            .transform(BulkheadOperator.of(bulkhead))
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .onErrorResume(TimeoutException::class.java, fallback)
            .onErrorResume(CallNotPermittedException::class.java, fallback)
            .onErrorResume(BulkheadFullException::class.java, fallback)
    }

    private fun <T> executeWithFallback(publisher: Flux<T>, fallback: (Throwable) -> Flux<T>): Flux<T> {
        return publisher
            .transform(TimeLimiterOperator.of(timeLimiter))
            .transform(BulkheadOperator.of(bulkhead))
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .onErrorResume(TimeoutException::class.java, fallback)
            .onErrorResume(CallNotPermittedException::class.java, fallback)
            .onErrorResume(BulkheadFullException::class.java, fallback)
    }


    private fun <T> execute(supplier: () -> T): T {
        return Decorators.ofSupplier(supplier)
            .withCircuitBreaker(circuitBreaker)
            .withBulkhead(bulkhead)
            .withRetry(retry)
            .get()
    }

    private fun <T> executeAsync(supplier: () -> CompletableFuture<T>): CompletableFuture<T> {
        return Decorators.ofCompletionStage(supplier)
            .withBulkhead(bulkhead)
            .withTimeLimiter(timeLimiter, scheduledExecutorService)
            .withCircuitBreaker(circuitBreaker)
            .withRetry(retry, scheduledExecutorService)
            .get().toCompletableFuture()
    }

    private fun <T> executeAsyncWithFallback(
        supplier: () -> CompletableFuture<T>,
        fallback: (Throwable) -> T
    ): CompletableFuture<T> {
        val expectedExceptionTypes = listOf(
            TimeoutException::class.java,
            CallNotPermittedException::class.java,
            BulkheadFullException::class.java
        )
        return Decorators.ofCompletionStage(supplier)
            .withBulkhead(bulkhead)
            .withTimeLimiter(timeLimiter, scheduledExecutorService)
            .withCircuitBreaker(circuitBreaker)
            .withFallback(expectedExceptionTypes, fallback)
            .get().toCompletableFuture()
    }

    private fun fallback(ex: Throwable): String = "Recovered: $ex"
    private fun monoFallback(ex: Throwable) = Mono.just("Recovered: $ex")
    private fun fluxFallback(ex: Throwable) = Flux.just("Recovered: $ex")
}
