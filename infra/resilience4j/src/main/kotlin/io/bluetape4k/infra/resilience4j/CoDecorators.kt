package io.bluetape4k.infra.resilience4j

import io.bluetape4k.infra.resilience4j.bulkhead.decorateSuspendBiFunction
import io.bluetape4k.infra.resilience4j.bulkhead.decorateSuspendFunction1
import io.bluetape4k.infra.resilience4j.cache.CoCache
import io.bluetape4k.infra.resilience4j.cache.decorateSuspendedFunction
import io.bluetape4k.infra.resilience4j.circuitbreaker.decorateSuspendBiFunction
import io.bluetape4k.infra.resilience4j.circuitbreaker.decorateSuspendFunction1
import io.bluetape4k.infra.resilience4j.ratelimiter.decorateSuspendBiFunction
import io.bluetape4k.infra.resilience4j.ratelimiter.decorateSuspendFunction1
import io.bluetape4k.infra.resilience4j.retry.decorateSuspendBiFunction
import io.bluetape4k.infra.resilience4j.retry.decorateSuspendFunction1
import io.bluetape4k.infra.resilience4j.timelimiter.decorateSuspendBiFunction
import io.bluetape4k.infra.resilience4j.timelimiter.decorateSuspendFunction1
import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.cache.Cache
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.kotlin.bulkhead.decorateSuspendFunction
import io.github.resilience4j.kotlin.circuitbreaker.decorateSuspendFunction
import io.github.resilience4j.kotlin.ratelimiter.decorateSuspendFunction
import io.github.resilience4j.kotlin.retry.decorateSuspendFunction
import io.github.resilience4j.kotlin.timelimiter.decorateSuspendFunction
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.timelimiter.TimeLimiter

/**
 * A resilience4j decorator builder for suspend functions (Kotlin coroutines)
 *
 * @see [io.github.resilience4j.decorators.Decorators]
 */
object CoDecorators {

    /**
     * `suspend () -> Unit` 에 대해 resilience4j compoenents를 decorate 합니다
     *
     * @param runnable 수행할 suspend 함수
     */
    fun ofRunnable(runnable: suspend () -> Unit): io.bluetape4k.infra.resilience4j.CoDecorators.CoDecoratorForFunction<Unit> =
        io.bluetape4k.infra.resilience4j.CoDecorators.CoDecoratorForFunction(runnable)

    /**
     * `suspend () -> T` 에 대해 resilience4j components를 decorate 합니다.
     *
     * @param T return type
     * @param supplier Suspendable supplier
     */
    fun <T : Any> ofSupplier(supplier: suspend () -> T): io.bluetape4k.infra.resilience4j.CoDecorators.CoDecoratorForFunction<T> =
        io.bluetape4k.infra.resilience4j.CoDecorators.CoDecoratorForFunction(supplier)

    /**
     * `suspend (T) -> Unit` 에 대해 resilience4j components를 decorate 합니다
     *
     * @param T input type
     * @param consumer Suspendable consumer
     */
    fun <T : Any> ofConsumer(consumer: suspend (T) -> Unit): (T) -> io.bluetape4k.infra.resilience4j.CoDecorators.CoDecoratorForFunction<Unit> =
        { input: T ->
            io.bluetape4k.infra.resilience4j.CoDecorators.ofRunnable { consumer(input) }
        }

    fun <T : Any, R : Any> ofFunction(function: suspend (T) -> R): (T) -> io.bluetape4k.infra.resilience4j.CoDecorators.CoDecoratorForFunction<R> =
        { input: T ->
            io.bluetape4k.infra.resilience4j.CoDecorators.ofSupplier { function(input) }
        }

    /**
     * `suspend (T) -> R` 에 대해 resilience4j components를 decorate 합니다.
     *
     * @param T input type
     * @param R return type
     * @param function Suspendable function
     */
    fun <T : Any, R : Any> ofFunction1(function: suspend (T) -> R) =
        CoDecoratorForFunction1(function)

    /**
     * `suspend (T, U) -> R` 에 대해 resilience4j components를 decorate 합니다.
     *
     * @param T first input type
     * @param U second input type
     * @param R return type
     * @param function Suspendable function
     */
    fun <T : Any, U : Any, R : Any> ofFunction2(function: suspend (T, U) -> R) =
        CoDecoratorForFunction2(function)

    /**
     * `suspend (T, U) -> Unit` 에 대해 resilience4j components를 decorate 합니다.
     *
     * @param T first input type
     * @param U second input type
     * @param consumer Suspendable bi consumer
     */
    fun <T : Any, U : Any> ofBiConsumer(consumer: suspend (T, U) -> Unit) =
        CoDecoratorForFunction2(consumer)


    class CoDecoratorForFunction<T>(private var supplier: suspend () -> T) {

        fun withCircuitBreaker(circuitBreaker: CircuitBreaker) = apply {
            supplier = circuitBreaker.decorateSuspendFunction(supplier)
        }

        fun withRetry(retry: Retry) = apply {
            supplier = retry.decorateSuspendFunction(supplier)
        }

        fun withRateLimit(rateLimiter: RateLimiter) = apply {
            supplier = rateLimiter.decorateSuspendFunction(supplier)
        }

        fun withBulkhead(bulkhead: Bulkhead) = apply {
            supplier = bulkhead.decorateSuspendFunction(supplier)
        }

        fun withTimeLimiter(timeLimiter: TimeLimiter) = apply {
            supplier = timeLimiter.decorateSuspendFunction(supplier)
        }

        fun decoreate(): suspend () -> T = supplier

        suspend fun get(): T = supplier()

        suspend fun invoke(): T = supplier()
    }

    class CoDecoratorForFunction1<T : Any, R : Any>(private var func: suspend (T) -> R) {

        fun withCircuitBreaker(circuitBreaker: CircuitBreaker) = apply {
            func = circuitBreaker.decorateSuspendFunction1(func)
        }

        fun withRetry(retry: Retry) = apply {
            func = retry.decorateSuspendFunction1(func)
        }

        fun withRateLimit(rateLimiter: RateLimiter) = apply {
            func = rateLimiter.decorateSuspendFunction1(func)
        }

        fun withBulkhead(bulkhead: Bulkhead) = apply {
            func = bulkhead.decorateSuspendFunction1(func)
        }

        fun withTimeLimiter(timeLimiter: TimeLimiter) = apply {
            func = timeLimiter.decorateSuspendFunction1(func)
        }

        /**
         * Cache를 제공합니다
         */
        fun withCache(cache: Cache<T, R>) = apply {
            func = cache.decorateSuspendedFunction(func)
        }

        fun withCoroutinesCache(coCache: CoCache<T, R>) = apply {
            func = coCache.decorateSuspendedFunction(func)
        }

        fun decoreate(): suspend (T) -> R = func

        suspend fun invoke(input: T): R = func(input)
    }

    class CoDecoratorForFunction2<T : Any, U : Any, R : Any>(private var func: suspend (T, U) -> R) {

        fun withCircuitBreaker(circuitBreaker: CircuitBreaker) = apply {
            func = circuitBreaker.decorateSuspendBiFunction(func)
        }

        fun withRetry(retry: Retry) = apply {
            func = retry.decorateSuspendBiFunction(func)
        }

        fun withRateLimit(rateLimiter: RateLimiter) = apply {
            func = rateLimiter.decorateSuspendBiFunction(func)
        }

        fun withBulkhead(bulkhead: Bulkhead) = apply {
            func = bulkhead.decorateSuspendBiFunction(func)
        }

        fun withTimeLimiter(timeLimiter: TimeLimiter) = apply {
            func = timeLimiter.decorateSuspendBiFunction(func)
        }

        fun decoreate(): suspend (T, U) -> R = func

        suspend fun invoke(t: T, u: U): R = func(t, u)
    }
}
