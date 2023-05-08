package io.bluetape4k.vertx

import io.vertx.core.Future
import java.util.concurrent.CompletableFuture

fun <T> Future<T>.toCompletableFuture(): CompletableFuture<T> {
    val promise = CompletableFuture<T>()

    onComplete { ar ->
        if (ar.succeeded()) {
            promise.complete(ar.result())
        } else {
            promise.completeExceptionally(ar.cause())
        }
    }

    return promise
}
