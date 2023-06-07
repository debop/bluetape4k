package io.bluetape4k.vertx.resilience4j

import io.github.resilience4j.ratelimiter.RateLimiter
import io.vertx.core.Future
import io.vertx.core.Promise

inline fun <T> RateLimiter.executeVertxFuture(crossinline supplier: () -> Future<T>): Future<T> {
    return decorateVertxFuture(supplier).invoke()
}

inline fun <T> RateLimiter.decorateVertxFuture(
    crossinline supplier: () -> Future<T>,
): () -> Future<T> = {
    val promise = Promise.promise<T>()

    try {
        RateLimiter.waitForPermission(this, 1)
        supplier.invoke()
            .onSuccess { promise.complete(it) }
            .onFailure { promise.fail(it) }
    } catch (e: Throwable) {
        promise.fail(e)
    }

    promise.future()
}
