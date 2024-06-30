package io.bluetape4k.vertx.resilience4j

import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.bulkhead.BulkheadFullException
import io.vertx.core.Future
import io.vertx.core.Promise

/**
 * Vert.x [Future]를 [Bulkhead]로 decorate 하여 실행합니다.
 */
inline fun <T> Bulkhead.executeVertxFuture(
    crossinline supplier: () -> Future<T>,
): Future<T> {
    return decorateVertxFuture(supplier).invoke()
}

/**
 * Vert.x [Future]를 [Bulkhead]로 decorate 합니다.
 */
inline fun <T> Bulkhead.decorateVertxFuture(
    crossinline supplier: () -> Future<T>,
): () -> Future<T> = {
    val promise = Promise.promise<T>()

    if (tryAcquirePermission()) {
        try {
            supplier().onComplete { ar ->
                onComplete()
                if (ar.succeeded()) {
                    promise.complete(ar.result())
                } else {
                    promise.fail(ar.cause())
                }
            }
        } catch (e: Throwable) {
            onComplete()
            promise.fail(e)
        }
    } else {
        promise.fail(BulkheadFullException.createBulkheadFullException(this))
    }

    promise.future()
}
