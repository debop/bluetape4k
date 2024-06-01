package io.bluetape4k.concurrent.virtualthread

import io.bluetape4k.concurrent.asCompletableFuture
import io.bluetape4k.concurrent.sequence
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object VirtualThreadExecutor: ExecutorService by Executors.newVirtualThreadPerTaskExecutor()

/**
 * Virtual thread 를 이용하여 비동기 작업을 수행합니다.
 *
 * @param runnable 비동기로 수행할 작업
 * @return [VirtualFuture] 인스턴스
 */
@JvmName("virtualFutureForRunnable")
fun async(
    executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor(),
    runnable: () -> Unit,
): VirtualFuture<Unit> {
    return VirtualFuture(executor.submit<Unit>(runnable))
}

/**
 * Virtual thread 를 이용하여 비동기 작업을 수행합니다.
 *
 * @param callable 비동기로 수행할 작업
 * @return [VirtualFuture] 인스턴스
 */
@JvmName("virtualFutureForCallable")
fun <T> virtualFuture(
    executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor(),
    callable: () -> T,
): VirtualFuture<T> {
    return VirtualFuture(executor.submit<T>(callable))
}

/**
 * 복수의 작업들을 Virtual thread 를 이용하여 비동기로 수행합니다.
 *
 * @param T
 * @param tasks
 * @param timeout
 * @return
 */
fun <T> virtualFutureAll(
    executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor(),
    tasks: Collection<() -> T>,
): VirtualFuture<List<T>> {
    val future = executor
        .invokeAll(tasks.map { Callable { it.invoke() } })
        .map { it.asCompletableFuture() }
        .sequence(executor)

    return VirtualFuture(future)
}

/**
 * 복수의 작업들을 Virtual thread 를 이용하여 비동기로 수행합니다.
 *
 * @param T
 * @param tasks
 * @param timeout
 * @return
 */
fun <T> virtualFutureAll(
    executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor(),
    tasks: Collection<() -> T>,
    timeout: Duration,
): VirtualFuture<List<T>> {
    val future = executor
        .invokeAll(
            tasks.map { Callable { it.invoke() } },
            timeout.toMillis(),
            TimeUnit.MILLISECONDS
        )
        .map { it.asCompletableFuture() }
        .sequence(executor)

    return VirtualFuture(future)
}


/**
 * 모든 [VirtualFuture]의 작업이 완료될 때가지 대기한다.
 */
fun <T> Iterable<VirtualFuture<T>>.awaitAll(): List<T> {
    return map { it.asCompletableFuture() }.sequence(VirtualThreadExecutor).get()
}

/**
 * 모든 [VirtualFuture]의 작업이 완료될 때가지 대기한다.
 */
fun <T> Iterable<VirtualFuture<T>>.awaitAll(timeout: Duration): List<T> {
    return map { it.asCompletableFuture() }.sequence(VirtualThreadExecutor)
        .get(timeout.toMillis(), TimeUnit.MILLISECONDS)
}

/**
 * 모든 [VirtualFuture]의 작업이 완료될 때까지 대기한다.
 */
fun <T> awaitAll(vararg vfutures: VirtualFuture<T>): List<T> {
    return vfutures.toList().awaitAll()
}
