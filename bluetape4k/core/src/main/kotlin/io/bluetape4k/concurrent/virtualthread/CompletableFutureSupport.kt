package io.bluetape4k.concurrent.virtualthread

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * 지정한 block을 Virtual Threads 를 이용하여 비동기로 실행하고, [CompletableFuture]를 반환합니다.
 */
inline fun <V: Any> virtualFutureOf(
    executor: Executor = Executors.newVirtualThreadPerTaskExecutor(),
    crossinline block: () -> V,
): CompletableFuture<V> =
    CompletableFuture.supplyAsync({ block() }, executor)
