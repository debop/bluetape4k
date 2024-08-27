package io.bluetape4k.vertx.resilience4j

import io.github.resilience4j.ratelimiter.RateLimiter
import io.vertx.core.Future
import io.vertx.core.Promise

/**
 * Vert.x [Future]를 [RateLimiter]로 decorate 하여 실행합니다.
 */
inline fun <T> RateLimiter.executeVertxFuture(
    crossinline supplier: () -> Future<T>,
): Future<T> {
    return decorateVertxFuture(supplier).invoke()
}

/**
 * Vert.x [Future]를 [RateLimiter]로 decorate 합니다.
 */
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
