package io.bluetape4k.redis.redisson.coroutines

import io.bluetape4k.concurrent.sequence
import kotlinx.coroutines.future.await
import org.redisson.api.RFuture
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool

/**
 * Coroutines 환경 하에서 결과를 기다리는 동안 suspend 합니다. (blocking 하지 않습니다)
 */
@Deprecated("use coAwait()", replaceWith = ReplaceWith("coAwait()"))
suspend inline fun <V> RFuture<V>.awaitSuspending(): V = toCompletableFuture().await()

/**
 * Coroutines 환경 하에서 결과를 기다리는 동안 suspend 합니다. (blocking 하지 않습니다)
 */
suspend fun <V> RFuture<V>.coAwait(): V = toCompletableFuture().await()

/**
 * [RFuture]의 컬렉션을 하나의 CompletableFuture로 변환합니다.
 *
 * ```
 * val rfutures: List<RFuture<User>> = ids.map { rmap.getAsync(it) }
 * val future: CompletableFuture<List<User>> = rfutures.sequence()
 * val users: List<User> = future.get()
 * ```
 */
fun <V> Iterable<RFuture<out V>>.sequence(
    executor: Executor = ForkJoinPool.commonPool(),
): CompletableFuture<List<V>> {
    return map { it.toCompletableFuture() }.sequence(executor)
}


/**
 * [RFuture]의 컬렉션을 결과를 모두 기다리고 List 로 반환한다
 *
 * ```
 * runBlocking {
 *     val rfutures: List<RFuture<User>> = ids.map { rmap.getAsync(it) }
 *     val users = rfutures.awaitAll()
 * }
 */
suspend fun <V> Collection<RFuture<out V>>.awaitAll(): List<V> {
    return sequence().await()
}
