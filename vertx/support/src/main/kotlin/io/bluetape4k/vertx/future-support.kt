package io.bluetape4k.vertx

import io.vertx.core.Future
import java.util.concurrent.CompletableFuture

fun <T> Future<T>.toCompletableFuture(): CompletableFuture<T> {
    val promise = CompletableFuture<T>()

    onComplete {
        if (it.succeeded()) {
            promise.complete(it.result())
        } else {
            promise.completeExceptionally(it.cause())
        }
    }

    return promise
}
