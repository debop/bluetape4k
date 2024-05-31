package io.bluetape4k.concurrent.virtualthread

import io.bluetape4k.concurrent.asCompletableFuture
import io.bluetape4k.concurrent.sequence
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object VirtualThreadExecutor: ExecutorService by Executors.newVirtualThreadPerTaskExecutor()

/**
 * 모든 [VirtualFuture]의 작업이 완료될 때가지 대기한다.
 */
fun <T> Iterable<VirtualFuture<T>>.awaitAll(): List<T> {
    return map { it.asCompletableFuture() }
        .sequence(VirtualThreadExecutor)
        .get()
}

/**
 * 모든 [VirtualFuture]의 작업이 완료될 때가지 대기한다.
 */
fun <T> Iterable<VirtualFuture<T>>.awaitAll(timeout: Duration): List<T> {
    return map { it.asCompletableFuture() }
        .sequence(VirtualThreadExecutor)
        .get(timeout.toMillis(), TimeUnit.MILLISECONDS)
}

/**
 * 모든 [VirtualFuture]의 작업이 완료될 때까지 대기한다.
 */
fun <T> awaitAll(vararg vfutures: VirtualFuture<T>): List<T> {
    return vfutures.toList().awaitAll()
}
