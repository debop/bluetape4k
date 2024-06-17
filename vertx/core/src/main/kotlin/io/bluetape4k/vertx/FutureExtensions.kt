package io.bluetape4k.vertx

import io.vertx.core.Future
import java.util.concurrent.CompletableFuture

/**
 * Vert.x [Future]를 [CompletableFuture]로 변환합니다.
 */
fun <T> Future<T>.toCompletableFuture(): CompletableFuture<T> {
    val promise = CompletableFuture<T>()

    onSuccess { result ->
        promise.complete(result)
    }
    onFailure { error ->
        promise.completeExceptionally(error)
    }

    return promise
}
