package io.bluetape4k.workshop.resilience4j.service

import io.bluetape4k.concurrent.futureOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.resilience4j.exception.BusinessException
import io.github.resilience4j.bulkhead.BulkheadFullException
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.github.resilience4j.timelimiter.annotation.TimeLimiter
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpServerErrorException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeoutException

@Component(value = "backendCService")
class BackendCService: Service {

    companion object: KLogging() {
        private const val BACKEND_C: String = "backendC"
    }

    @CircuitBreaker(name = BACKEND_C)
    @Bulkhead(name = BACKEND_C)
    @Retry(name = BACKEND_C)
    override fun failure(): String {
        throw HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception")
    }

    @CircuitBreaker(name = BACKEND_C, fallbackMethod = "fallback")
    override fun failureWithFallback(): String {
        return failure()
    }

    @CircuitBreaker(name = BACKEND_C)
    @Bulkhead(name = BACKEND_C)
    override fun ignoreException(): String {
        throw BusinessException("이 예외는 backend C 의 CircuitBreaker에 의해 무시됩니다")
    }


    @CircuitBreaker(name = BACKEND_C)
    @Bulkhead(name = BACKEND_C)
    @Retry(name = BACKEND_C)
    override fun success(): String {
        return "Hello World from backend C"
    }

    @CircuitBreaker(name = BACKEND_C)
    @Bulkhead(name = BACKEND_C)
    override fun successException(): String {
        throw HttpServerErrorException(HttpStatus.BAD_REQUEST, "This is a remote client exception")
    }

    @TimeLimiter(name = BACKEND_C)
    @CircuitBreaker(name = BACKEND_C)
    @Retry(name = BACKEND_C)
    override fun fluxSuccess(): Flux<String> {
        return Flux.just("Hello", "World")
    }

    @CircuitBreaker(name = BACKEND_C)
    @Bulkhead(name = BACKEND_C)
    @Retry(name = BACKEND_C)
    override fun fluxFailure(): Flux<String> {
        return Flux.error(IOException("BAM!"))
    }

    @TimeLimiter(name = BACKEND_C)
    @CircuitBreaker(name = BACKEND_C, fallbackMethod = "fluxFallback")
    override fun fluxTimeout(): Flux<String> {
        return Flux
            .just("Hello World from backend C")
            .delayElements(Duration.ofSeconds(10))
    }

    @TimeLimiter(name = BACKEND_C)
    @CircuitBreaker(name = BACKEND_C)
    @Bulkhead(name = BACKEND_C)
    @Retry(name = BACKEND_C)
    override fun monoSuccess(): Mono<String> {
        return Mono.just("Hello World from backend C")
    }

    @CircuitBreaker(name = BACKEND_C)
    @Bulkhead(name = BACKEND_C)
    @Retry(name = BACKEND_C)
    override fun monoFailure(): Mono<String> {
        return Mono.error(IOException("BAM!"))
    }

    @TimeLimiter(name = BACKEND_C)
    @Bulkhead(name = BACKEND_C)
    @CircuitBreaker(name = BACKEND_C, fallbackMethod = "monoFallback")
    override fun monoTimeout(): Mono<String> {
        return Mono
            .just("Hello World from backend C")
            .delayElement(Duration.ofSeconds(10))
    }

    @Bulkhead(name = BACKEND_C, type = Bulkhead.Type.THREADPOOL)
    @TimeLimiter(name = BACKEND_C)
    @CircuitBreaker(name = BACKEND_C)
    @Retry(name = BACKEND_C)
    override fun futureSuccess(): CompletableFuture<String> {
        return CompletableFuture.completedFuture("Hello World from backend C")
    }

    @Bulkhead(name = BACKEND_C, type = Bulkhead.Type.THREADPOOL)
    @TimeLimiter(name = BACKEND_C)
    @CircuitBreaker(name = BACKEND_C)
    @Retry(name = BACKEND_C)
    override fun futureFailure(): CompletableFuture<String> {
        return CompletableFuture.failedFuture(IOException("BAM!"))
    }

    @Bulkhead(name = BACKEND_C, type = Bulkhead.Type.THREADPOOL)
    @TimeLimiter(name = BACKEND_C)
    @CircuitBreaker(name = BACKEND_C, fallbackMethod = "futureFallback")
    override fun futureTimeout(): CompletableFuture<String> {
        return futureOf {
            Thread.sleep(5000)
            "Hello World from backend C"
        }
    }

    private fun fallback(ex: HttpServerErrorException): String {
        return "Recovered HttpServerErrorException: ${ex.message}"
    }

    private fun fallback(ex: Exception): String {
        return "Recovered: $ex"
    }

    private fun fluxFallback(ex: Exception): Flux<String> {
        return Flux.just("Recovered: $ex")
    }

    private fun monoFallback(ex: Exception): Mono<String> {
        return Mono.just("Recovered: $ex")
    }

    private fun futureFallback(ex: TimeoutException): CompletableFuture<String> {
        return CompletableFuture.completedFuture("Recovered TimeoutException: $ex")
    }

    private fun futureFallback(ex: BulkheadFullException): CompletableFuture<String> {
        return CompletableFuture.completedFuture("Recovered specific BulkheadFullException: $ex")
    }

    private fun futureFallback(ex: CallNotPermittedException): CompletableFuture<String> {
        return CompletableFuture.completedFuture("Recovered specific CallNotPermittedException: $ex")
    }
}
