package io.bluetape4k.vertx.resilience4j

import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.bulkhead.BulkheadFullException
import io.vertx.core.Future
import io.vertx.core.Promise

inline fun <T> Bulkhead.executeVertxFuture(
    crossinline supplier: () -> Future<T>,
): Future<T> {
    return decorateVertxFuture(supplier).invoke()
}

inline fun <T> Bulkhead.decorateVertxFuture(
    crossinline supplier: () -> Future<T>,
): () -> Future<T> = {
    val promise = Promise.promise<T>()

    if (tryAcquirePermission()) {
        try {
            supplier().onComplete { ar ->
                onComplete()
                if (ar.failed()) {
                    promise.fail(ar.cause())
                } else {
                    promise.complete(ar.result())
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