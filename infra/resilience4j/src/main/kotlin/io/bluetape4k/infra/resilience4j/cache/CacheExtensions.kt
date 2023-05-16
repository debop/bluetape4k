package io.bluetape4k.infra.resilience4j.cache

import io.github.resilience4j.cache.Cache
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

// resilience4j 1.1.+ 에서는 function1 에 대해 cache를 제공하지 않는다.
// 이를 확장하기 위해 기능을 추가했습니다.

inline fun <K, V> Cache<K, V>.decorateFunction(
    crossinline func: (K) -> V,
): (K) -> V = { key: K ->
    this.computeIfAbsent(key!!) { func(key) }
}

fun <K, V> ((K) -> CompletionStage<V>).cache(cache: Cache<K, V>): (K) -> CompletionStage<V> {
    return cache.decorateCompletionStage(this)
}

fun <K, V> Cache<K, V>.decorateFunction1(func: (K) -> V): (K) -> V = { key: K ->
    this.computeIfAbsent(key!!) { func(key) }
}

@Suppress("UNCHECKED_CAST")
fun <K, V> Cache<K, V>.decorateCompletionStage(
    func: (K) -> CompletionStage<V>,
): (K) -> CompletionStage<V> = { key: K ->
    decorateCompletableFutureFunction { func(key).toCompletableFuture() } as CompletionStage<V>
}

inline fun <K, V> Cache<K, V>.decorateCompletableFutureFunction(
    crossinline func: (K) -> CompletableFuture<V>,
): (K) -> CompletableFuture<V> = { key: K ->

    val promise = CompletableFuture<V>()
    val cachedValue = Optional.ofNullable(computeIfAbsent(key!!) { null })

    cachedValue.ifPresentOrElse({ promise.complete(it) }) {
        func(key)
            .whenComplete { result, error ->
                if (error != null) {
                    promise.completeExceptionally(error.cause ?: error)
                } else {
                    if (result != null) {
                        this.computeIfAbsent(key) { result }
                    }
                    promise.complete(result)
                }
            }
    }

    promise
}
