package io.bluetape4k.concurrent.virtualthread

import io.bluetape4k.concurrent.asCompletableFuture
import io.bluetape4k.concurrent.sequence
import io.bluetape4k.logging.KLogging
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * Java 21의 Virtual Thread를 이용하여 비동기 작업을 수행하는 [Future]
 */
class VirtualFuture<T>(
    private val future: Future<T>,
): Future<T> by future {

    companion object: KLogging() {
        /**
         * Virtual Thread를 사용하는 Executor
         */
        private val executor = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("vfuture-", 0).factory()
        )

        /**
         * Virtual thread 를 이용하여 비동기 작업을 수행합니다.
         *
         * @param callable 비동기로 수행할 작업
         * @return [VirtualFuture] 인스턴스
         */
        @Deprecated("Use virtualFuture(callable: () -> T) instead", ReplaceWith("virtualFuture(callable)"))
        @JvmName("asyncForCallable")
        fun <T> async(callable: () -> T): VirtualFuture<T> {
            return VirtualFuture(executor.submit<T>(callable))
        }

        /**
         * Virtual thread 를 이용하여 비동기 작업을 수행합니다.
         *
         * @param runnable 비동기로 수행할 작업
         * @return [VirtualFuture] 인스턴스
         */
        @Deprecated("Use virtualFuture(runnable: () -> Unit) instead", ReplaceWith("virtualFuture(runnable)"))
        @JvmName("asyncForRunnable")
        fun async(runnable: () -> Unit): VirtualFuture<Unit> {
            return VirtualFuture(executor.submit<Unit>(runnable))
        }

        /**
         * 복수의 작업들을 Virtual thread 를 이용하여 비동기로 수행합니다.
         *
         * @param tasks 비동기로 수행할 작업 목록
         */
        @Deprecated("Use virtualFutureAll(tasks: Collection<() -> T>) instead", ReplaceWith("virtualFutureAll(tasks)"))
        fun <T> asyncAll(tasks: Collection<() -> T>): VirtualFuture<List<T>> {
            val future = executor.invokeAll(tasks.map { Callable { it.invoke() } }).map { it.asCompletableFuture() }
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
        @Deprecated(
            "Use virtualFutureAll(tasks: Collection<() -> T>, timeout) instead",
            ReplaceWith("virtualFutureAll(tasks, timeout)")
        )
        fun <T> asyncAll(tasks: Collection<() -> T>, timeout: Duration): VirtualFuture<List<T>> {
            val future = executor.invokeAll(
                tasks.map { Callable { it.invoke() } }, timeout.toMillis(), TimeUnit.MILLISECONDS
            ).map { it.asCompletableFuture() }.sequence(executor)

            return VirtualFuture(future)
        }
    }

    /**
     * Virtual thread 가 완료되기를 기다림
     *
     * @return 작업 결과
     */
    fun await(): T {
        return future.get()
    }

    /**
     * Virtual thread 가 완료되기를 기다림
     *
     * @param timeout 대기 시간
     * @return 작업 결과
     */
    fun await(timeout: Duration): T {
        return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS)
    }

    /**
     * [CompletableFuture]로 변환합니다.
     *
     * @return [CompletableFuture] 인스턴스
     */
    fun toCompletableFuture(): CompletableFuture<T> {
        return future.asCompletableFuture()
    }
}
