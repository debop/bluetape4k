package io.bluetape4k.utils.resilience4j.cache

import io.github.resilience4j.cache.Cache
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

// resilience4j 1.1.+ 에서는 function1 에 대해 cache를 제공하지 않는다.
// 이를 확장하기 위해 기능을 추가했습니다.


fun <K: Any, V> Cache<K, V>.decorateFunction1(func: (K) -> V): (K) -> V = { key: K ->
    this.computeIfAbsent(key) { func(key) }
}

@Suppress("UNCHECKED_CAST")
fun <K: Any, V> Cache<K, V>.decorateCompletionStage(
    func: (K) -> CompletionStage<V>,
): (K) -> CompletionStage<V> = { key: K ->
    decorateCompletableFutureFunction { func(key).toCompletableFuture() } as CompletionStage<V>
}

fun <K: Any, V> Cache<K, V>.decorateCompletableFutureFunction(
    func: (K) -> CompletableFuture<V>,
): (K) -> CompletableFuture<V> = { key: K ->
    val promise = CompletableFuture<V>()

    val value = Optional.ofNullable(this.computeIfAbsent(key) { null })

    if (value.isPresent) {
        promise.complete(value.get())
    } else {
        func(key).whenComplete { result, error ->
            if (error != null) {
                promise.completeExceptionally(error)
            } else {
                this.computeIfAbsent(key) { result }
                promise.complete(result)
            }
        }
    }

    promise
}
