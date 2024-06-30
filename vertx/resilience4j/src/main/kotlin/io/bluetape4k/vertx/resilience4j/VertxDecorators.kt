package io.bluetape4k.vertx.resilience4j

import io.bluetape4k.logging.KLogging
import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.timelimiter.TimeLimiter
import io.vertx.core.Future
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Resilience4j의 다양한 요소들을 Decorator로 사용할 수 있도록 지원합니다.
 */
object VertxDecorators: KLogging() {

    fun <T> ofSupplier(supplier: () -> Future<T>): CoVertxDecorateSupplier<T> {
        return CoVertxDecorateSupplier(supplier)
    }

    class CoVertxDecorateSupplier<T>(private var supplier: () -> Future<T>) {

        fun withBulkhead(bulkhead: Bulkhead) = apply {
            supplier = bulkhead.decorateVertxFuture(supplier)
        }

        fun withCircuitBreaker(circuitBreaker: CircuitBreaker) = apply {
            supplier = circuitBreaker.decorateVertxFuture(supplier)
        }

        fun withRateLimiter(rateLimiter: RateLimiter) = apply {
            supplier = rateLimiter.decorateVertxFuture(supplier)
        }

        fun withRetry(
            retry: Retry,
            scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
        ) = apply {
            supplier = retry.decorateVertxFuture(scheduler, supplier)
        }

        fun withTimeLimiter(
            timeLimiter: TimeLimiter,
            scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
        ) = apply {
            supplier = timeLimiter.decorateVertxFuture(scheduler, supplier)
        }

        fun withFallback(handler: (T?, Throwable?) -> T) = apply {
            supplier = supplier.recover(handler)
        }

        fun withFallback(exceptionHandler: (Throwable?) -> T) = apply {
            supplier = supplier.recover(exceptionHandler)
        }

        fun withFallback(
            resultPredicate: (T) -> Boolean,
            resultHandler: (T) -> T,
        ) = apply {
            supplier = supplier.recover(resultPredicate, resultHandler)
        }

        fun withFallback(
            exceptionType: Class<out Throwable>,
            exceptionHandler: (Throwable?) -> T,
        ) = apply {
            supplier = supplier.recover(exceptionType, exceptionHandler)
        }

        fun withFallback(
            exceptionTypes: Iterable<Class<out Throwable>>,
            exceptionHandler: (Throwable?) -> T,
        ) = apply {
            supplier = supplier.recover(exceptionTypes, exceptionHandler)
        }

        fun decorate(): () -> Future<T> = supplier

        fun invoke(): Future<T> = decorate().invoke()
    }
}
