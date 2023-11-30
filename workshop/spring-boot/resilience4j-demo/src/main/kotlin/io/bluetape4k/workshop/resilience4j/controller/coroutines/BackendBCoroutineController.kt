package io.bluetape4k.workshop.resilience4j.controller.coroutines

import io.bluetape4k.infra.resilience4j.CoDecorators
import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.resilience4j.service.coroutines.CoroutineService
import io.github.resilience4j.bulkhead.BulkheadRegistry
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.kotlin.bulkhead.bulkhead
import io.github.resilience4j.kotlin.circuitbreaker.circuitBreaker
import io.github.resilience4j.kotlin.retry.retry
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.retry.RetryRegistry
import io.github.resilience4j.timelimiter.TimeLimiterRegistry
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Executors

@RestController
@RequestMapping("/coroutine/backendB")
class BackendBCoroutineController(
    @Qualifier("backendBCoroutineService") private val businessBCoroutineService: CoroutineService,
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

    @GetMapping("suspendSuccess")
    suspend fun suspendSuccess(): String = execute { businessBCoroutineService.suspendSuccess() }

    @GetMapping("suspendFailure")
    suspend fun suspendFailure(): String = execute { businessBCoroutineService.suspendFailure() }

    @GetMapping("suspendTimeout")
    suspend fun suspendTimeout(): String = execute { businessBCoroutineService.suspendTimeout() }

    @GetMapping("flowSuccess")
    fun flowSuccess(): Flow<String> = execute(businessBCoroutineService.flowSuccess())

    @GetMapping("flowFailure")
    fun flowFailure(): Flow<String> = execute(businessBCoroutineService.flowFailure())

    @GetMapping("flowTimeout")
    fun flowTimeout(): Flow<String> = execute(businessBCoroutineService.flowTimeout())

    private suspend fun <T> execute(block: suspend () -> T): T {
        return CoDecorators.ofSupplier(block)
            .withCircuitBreaker(circuitBreaker)
            .withBulkhead(bulkhead)
            .withRetry(retry)
            .get()
    }

    private fun <T> execute(flow: Flow<T>): Flow<T> {
        return flow
            .bulkhead(bulkhead)
            .circuitBreaker(circuitBreaker)
            .retry(retry)
    }
}
