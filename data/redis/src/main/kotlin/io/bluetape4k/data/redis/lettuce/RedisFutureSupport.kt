package io.bluetape4k.data.redis.lettuce

import io.bluetape4k.concurrent.sequence
import io.lettuce.core.RedisFuture
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool

/**
 * Awaits for completion of [RedisFuture] without blocking a thread.
 */
suspend fun <T> RedisFuture<T>.await(): T {
    return asDeferred().await()
}

suspend fun <T> Collection<RedisFuture<out T>>.awaitAll(): List<T> {
    return when {
        this.isEmpty() -> emptyList()
        else           -> sequence().await()
    }
}

fun <T> Iterable<RedisFuture<out T>>.sequence(
    executor: Executor = ForkJoinPool.commonPool(),
): CompletableFuture<List<T>> {
    return map { it.toCompletableFuture() }.sequence(executor)
}
