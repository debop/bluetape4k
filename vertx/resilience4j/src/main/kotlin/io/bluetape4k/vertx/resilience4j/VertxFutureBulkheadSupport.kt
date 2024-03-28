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
    val self = this
    val promise = Promise.promise<T>()

    if (tryAcquirePermission()) {
        supplier.invoke()
            .onComplete { ar ->
                self.onComplete()
                if (ar.succeeded()) {
                    promise.complete(ar.result())
                } else {
                    promise.fail(ar.cause())
                }
            }
    } else {
        promise.fail(BulkheadFullException.createBulkheadFullException(this))
    }

    promise.future()
}
