package io.bluetape4k.concurrent

import io.bluetape4k.core.requirePositiveNumber
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.failedFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Semaphore


/**
 * `Semaphore` 를 이용하여 비동기 작업을 제한된 숫자만큼만 수행되도록 합니다.
 *
 * @property maxConcurrency
 * @property maxQueueSize
 */
class ConcurrencyReducer<T> private constructor(
    private val maxConcurrency: Int,
    private val maxQueueSize: Int,
) {
    companion object: KLogging() {
        @JvmStatic
        operator fun <T> invoke(maxConcurrency: Int, maxQueueSize: Int): ConcurrencyReducer<T> {
            maxConcurrency.requirePositiveNumber("maxConcurrency")
            maxQueueSize.requirePositiveNumber("maxQueueSize")

            return ConcurrencyReducer(maxConcurrency, maxQueueSize)
        }
    }

    private val queue: BlockingQueue<Job<T>> = ArrayBlockingQueue(maxQueueSize)
    private val limit: Semaphore = Semaphore(maxConcurrency)

    val queuedCount: Int get() = queue.size
    val activeCount: Int get() = maxConcurrency - limit.availablePermits()
    val remainingQueueCapacity: Int get() = queue.remainingCapacity()
    val remainingActiveCapacity: Int get() = limit.availablePermits()

    /**
     * 비동기 작업을 추가합니다.
     * 큐가 꽉 찬 경우에는 `CapacityReachedException`이 발생합니다.
     *
     * @param task
     * @return
     */
    fun add(task: () -> CompletionStage<T>?): CompletableFuture<T> {
        val promise = CompletableFuture<T>()
        val job = Job(task, promise)

        if (!queue.offer(job)) {
            return failedFuture(CapacityReachedException("Queue size has reached capacity: $maxQueueSize"))
        }
        pump()
        return promise
    }

    private fun grabJob(): Job<T>? {
        if (!limit.tryAcquire()) {
            return null
        }
        val job = queue.poll()
        if (job != null) {
            return job
        }
        limit.release()
        return null
    }

    private fun pump() {
        var job = grabJob()
        while (job != null) {
            if (job.promise.isCancelled) {
                limit.release()
            } else {
                invoke(job)
            }
            job = grabJob()
        }
    }

    private fun invoke(job: Job<T>) {
        val future: CompletionStage<T>?
        try {
            future = job.task.invoke()
            if (future == null) {
                log.debug { "task result is null." }
                limit.release()
                job.promise.completeExceptionally(NullPointerException())
                return
            }
        } catch (e: Throwable) {
            limit.release()
            job.promise.completeExceptionally(e)
            return
        }

        future.whenComplete { result, error ->
            limit.release()
            if (error != null) {
                job.promise.completeExceptionally(error)
            } else {
                job.promise.complete(result)
            }
            pump()
        }
    }

    private data class Job<T>(
        val task: () -> CompletionStage<T>?,
        val promise: CompletableFuture<T>,
    )

    class CapacityReachedException: RuntimeException {
        constructor(): super()
        constructor(message: String): super(message)
        constructor(message: String, cause: Throwable?): super(message, cause)
        constructor(cause: Throwable?): super(cause)
    }
}
