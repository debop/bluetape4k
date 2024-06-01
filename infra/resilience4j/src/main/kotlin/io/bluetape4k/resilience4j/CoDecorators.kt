package io.bluetape4k.resilience4j

import io.bluetape4k.resilience4j.bulkhead.decorateSuspendBiFunction
import io.bluetape4k.resilience4j.bulkhead.decorateSuspendFunction1
import io.bluetape4k.resilience4j.cache.CoCache
import io.bluetape4k.resilience4j.cache.decorateSuspendedFunction
import io.bluetape4k.resilience4j.circuitbreaker.decorateSuspendBiFunction
import io.bluetape4k.resilience4j.circuitbreaker.decorateSuspendFunction1
import io.bluetape4k.resilience4j.ratelimiter.decorateSuspendBiFunction
import io.bluetape4k.resilience4j.ratelimiter.decorateSuspendFunction1
import io.bluetape4k.resilience4j.retry.decorateSuspendBiFunction
import io.bluetape4k.resilience4j.retry.decorateSuspendFunction1
import io.bluetape4k.resilience4j.timelimiter.decorateSuspendBiFunction
import io.bluetape4k.resilience4j.timelimiter.decorateSuspendFunction1
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
import kotlin.reflect.KClass

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
    fun ofRunnable(runnable: suspend () -> Unit): CoDecoratorForSupplier<Unit> {
        return CoDecoratorForSupplier(runnable)
    }

    /**
     * `suspend () -> T` 에 대해 resilience4j components를 decorate 합니다.
     *
     * @param T return type
     * @param supplier Suspendable supplier
     */
    fun <T> ofSupplier(supplier: suspend () -> T): CoDecoratorForSupplier<T> {
        return CoDecoratorForSupplier(supplier)
    }

    /**
     * `suspend (T) -> Unit` 에 대해 resilience4j components를 decorate 합니다
     *
     * @param T input type
     * @param consumer Suspendable consumer
     */
    inline fun <T> ofConsumer(
        crossinline consumer: suspend (T) -> Unit,
    ): (T) -> CoDecoratorForSupplier<Unit> = { input: T ->
        CoDecorators.ofRunnable { consumer(input) }
    }

    inline fun <T, R> ofFunction(
        crossinline function: suspend (T) -> R,
    ): (T) -> CoDecoratorForSupplier<R> = { input: T ->
        CoDecorators.ofSupplier { function(input) }
    }

    /**
     * `suspend (T) -> R` 에 대해 resilience4j components를 decorate 합니다.
     *
     * @param T input type
     * @param R return type
     * @param function Suspendable function
     */
    fun <T, R> ofFunction1(
        function: suspend (T) -> R,
    ): CoDecoratorForFunction1<T, R> {
        return CoDecoratorForFunction1(function)
    }

    /**
     * `suspend (T, U) -> R` 에 대해 resilience4j components를 decorate 합니다.
     *
     * @param T first input type
     * @param U second input type
     * @param R return type
     * @param function Suspendable function
     */
    fun <T, U, R> ofFunction2(
        function: suspend (T, U) -> R,
    ): CoDecoratorForFunction2<T, U, R> {
        return CoDecoratorForFunction2(function)
    }

    /**
     * `suspend (T, U) -> Unit` 에 대해 resilience4j components를 decorate 합니다.
     *
     * @param T first input type
     * @param U second input type
     * @param consumer Suspendable bi consumer
     */
    fun <T, U> ofBiConsumer(consumer: suspend (T, U) -> Unit): CoDecoratorForFunction2<T, U, Unit> {
        return CoDecoratorForFunction2(consumer)
    }


    class CoDecoratorForSupplier<T>(private var supplier: suspend () -> T) {

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

        fun <K> withCache(cache: Cache<K, T>): CoDecoratorForFunction1<K, T> {
            return CoDecorators.ofFunction1(cache.decorateSuspendedFunction { supplier() })
        }

        fun <K> withCoroutineCache(cache: Cache<K, T>): CoDecoratorForFunction1<K, T> {
            return CoDecorators.ofFunction1(cache.decorateSuspendedFunction { supplier() })
        }

        fun withFallback(handler: suspend (T?, Throwable?) -> T) = apply {
            supplier = supplier.andThen(handler)
        }

        fun withFallback(
            resultPredicate: suspend (T) -> Boolean,
            resultHandler: suspend (T) -> T,
        ) = apply {
            supplier = supplier.recover(resultPredicate, resultHandler)
        }

        fun withFallback(exceptionHandler: suspend (Throwable?) -> T) = apply {
            supplier = supplier.recover(exceptionHandler)
        }

        fun withFallback(
            exceptionType: KClass<Throwable>,
            exceptionHandler: suspend (Throwable?) -> T,
        ) = apply {
            supplier = supplier.recover(exceptionType, exceptionHandler)
        }


        fun withFallback(
            exceptionTypes: Iterable<Class<Throwable>>,
            exceptionHandler: suspend (Throwable?) -> T,
        ) = apply {
            supplier = supplier.recover(exceptionTypes, exceptionHandler)
        }

        fun decoreate(): suspend () -> T = supplier

        suspend fun get(): T = supplier()

        suspend fun invoke(): T = supplier()
    }

    class CoDecoratorForFunction1<T, R>(private var func: suspend (T) -> R) {

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

    class CoDecoratorForFunction2<T, U, R>(private var func: suspend (T, U) -> R) {

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
