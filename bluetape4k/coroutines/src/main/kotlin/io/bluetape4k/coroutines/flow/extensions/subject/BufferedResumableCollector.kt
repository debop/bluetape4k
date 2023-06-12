package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.FlowCollector
import kotlin.coroutines.cancellation.CancellationException

/**
 * `capacity` 만큼 버퍼렁을 할 수 있는 [FlowCollector]
 *
 * @param capacity 버퍼 크기
 */
class BufferedResumableCollector<T> private constructor(capacity: Int): Resumable() {

    companion object: KLogging() {
        operator fun <T> invoke(capacity: Int): BufferedResumableCollector<T> {
            return BufferedResumableCollector(capacity.coerceAtLeast(1))
        }
    }

    private val queue: SpscArrayQueue<T> = SpscArrayQueue(capacity)

    private val done = atomic(false)
    private val cancelled = atomic(false)

    @Volatile
    private var error: Throwable? = null

    private val available = atomic(0L)

    private val valueReady = Resumable()

    private val output: Array<Any?> = Array(1) { null }
    private val limit: Int = capacity - (capacity shr 2)

    suspend fun next(value: T) {
        while (!cancelled.value) {
            if (queue.offer(value)) {
                if (available.getAndIncrement() == 0L) {
                    valueReady.resume()
                }
                break
            }
            await()
        }
        if (cancelled.value) {
            throw CancellationException("Cancel in next.")
        }
    }

    fun error(ex: Throwable?) {
        error = ex
        done.value = true
        valueReady.resume()
    }

    fun complete() {
        done.value = true
        valueReady.resume()
    }

    suspend fun drain(
        collector: FlowCollector<T>,
        onCrash: ((BufferedResumableCollector<T>) -> Unit)? = null,
    ) {
        var consumed = 0L
        val limit = this.limit.toLong()

        while (true) {
            val ne = !queue.poll(output)

            if (done.value && ne) {
                error?.let { throw it }
                break
            }

            // item exists in buffer
            if (!ne) {
                try {
                    @Suppress("UNCHECKED_CAST")
                    collector.emit(output[0] as T)
                } catch (ex: Throwable) {
                    onCrash?.invoke(this)
                    cancelled.value = true
                    resume()

                    throw ex
                }

                if (consumed++ == limit) {
                    available.addAndGet(-consumed)
                    consumed = 0L
                    resume()
                }

                continue
            }

            if (available.addAndGet(-consumed) == 0L) {
                resume()
                valueReady.await()
            }
            consumed = 0L
        }
    }
}
