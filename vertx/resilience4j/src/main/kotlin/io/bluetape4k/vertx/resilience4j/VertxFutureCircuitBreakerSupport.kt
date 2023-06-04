package io.bluetape4k.vertx.resilience4j

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.vertx.core.Future
import io.vertx.core.Promise
import java.util.concurrent.TimeUnit

inline fun <T> CircuitBreaker.executeVertxFuture(
    crossinline supplier: () -> Future<T>,
): Future<T> {
    return decorateVertxFuture(supplier).invoke()
}

inline fun <T> CircuitBreaker.decorateVertxFuture(
    crossinline supplier: () -> Future<T>,
): () -> Future<T> = {
    val promise = Promise.promise<T>()

    if (!tryAcquirePermission()) {
        promise.fail(CallNotPermittedException.createCallNotPermittedException(this))
    } else {
        val start = System.nanoTime()
        try {
            supplier.invoke()
                .onComplete { ar ->
                    val durationInNanos = System.nanoTime() - start
                    if (ar.succeeded()) {
                        onSuccess(durationInNanos, TimeUnit.NANOSECONDS)
                        promise.complete(ar.result())
                    } else {
                        onError(durationInNanos, TimeUnit.NANOSECONDS, ar.cause())
                        promise.fail(ar.cause())
                    }
                }
        } catch (e: Exception) {
            val durationInNanos = System.nanoTime() - start
            onError(durationInNanos, TimeUnit.NANOSECONDS, e)
            promise.fail(e)
        }
    }

    promise.future()
}
