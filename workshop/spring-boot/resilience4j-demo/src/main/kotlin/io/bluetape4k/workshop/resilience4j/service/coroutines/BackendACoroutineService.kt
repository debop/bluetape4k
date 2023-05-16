package io.bluetape4k.workshop.resilience4j.service.coroutines

import io.bluetape4k.logging.KLogging
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.time.delay
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.Duration

@Component(value = "backendACoroutineService")
class BackendACoroutineService: CoroutineService {

    companion object: KLogging() {
        const val BACKEND_A: String = "backendA"
    }

    // NOTE: TimeLimiter 는 suspend 함수를 지원하지 않습니다.

    // @TimeLimiter(name = BACKEND_A)
    @CircuitBreaker(name = BACKEND_A)
    @Bulkhead(name = BACKEND_A)
    @Retry(name = BACKEND_A)
    override suspend fun suspendSuccess(): String {
        return "Hello World from backend A"
    }

    @CircuitBreaker(name = BACKEND_A)
    @Bulkhead(name = BACKEND_A)
    @Retry(name = BACKEND_A)
    override suspend fun suspendFailure(): String {
        throw IOException("BAM!")
    }

    // @TimeLimiter(name = BACKEND_A)
    @Bulkhead(name = BACKEND_A)
    @CircuitBreaker(name = BACKEND_A, fallbackMethod = "suspendFallback")
    override suspend fun suspendTimeout(): String {
        delay(Duration.ofSeconds(10))
        return "Hello World from backend A"
    }

    // @TimeLimiter(name = BACKEND_A)
    @CircuitBreaker(name = BACKEND_A)
    @Retry(name = BACKEND_A)
    override fun flowSuccess(): Flow<String> {
        return flowOf("Hello", "World")
    }

    @CircuitBreaker(name = BACKEND_A)
    @Bulkhead(name = BACKEND_A)
    @Retry(name = BACKEND_A)
    override fun flowFailure(): Flow<String> {
        return flowOf("Hello", "World")
            .onStart { throw IOException("BAM!") }
    }

    // @TimeLimiter(name = BACKEND_A)
    @CircuitBreaker(name = BACKEND_A, fallbackMethod = "flowFallback")
    override fun flowTimeout(): Flow<String> {
        return flow {
            delay(Duration.ofSeconds(10))
            emit("Hello World from backend A")
        }
    }

    private suspend fun suspendFallback(ex: Exception): String {
        return "Recovered: $ex"
    }

    private fun flowFallback(ex: Exception): Flow<String> {
        return flowOf("Recovered: $ex")
    }
}
