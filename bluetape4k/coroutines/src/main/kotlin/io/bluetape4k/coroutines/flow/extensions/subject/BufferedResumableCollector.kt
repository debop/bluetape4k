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

    private var done by atomic(false)
    private var cancelled by atomic(false)

    private var error: Throwable? = null

    private var _available = atomic(0L)
    private val available by _available

    private val valueReady = Resumable()

    private val output: Array<Any?> = Array(1) { null }
    private val limit: Int = capacity - (capacity shr 2)

    suspend fun next(value: T) {
        while (!cancelled) {
            if (queue.offer(value)) {
                if (_available.getAndIncrement() == 0L) {
                    valueReady.resume()
                }
                break
            }
            await()
        }
        if (cancelled) {
            throw CancellationException("Cancel in next.")
        }
    }

    fun error(ex: Throwable?) {
        error = ex
        done = true
        valueReady.resume()
    }

    fun complete() {
        done = true
        valueReady.resume()
    }

    suspend fun drain(
        collector: FlowCollector<T>,
        onCrash: ((BufferedResumableCollector<T>) -> Unit)? = null
    ) {
        var consumed = 0L
        val limit = this.limit.toLong()

        while (true) {
            val ne = !queue.poll(output)

            if (done && ne) {
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
                    cancelled = true
                    resume()

                    throw ex
                }

                if (consumed++ == limit) {
                    _available.addAndGet(-consumed)
                    consumed = 0L
                    resume()
                }

                continue
            }

            if (_available.addAndGet(-consumed) == 0L) {
                resume()
                valueReady.await()
            }
            consumed = 0L
        }
    }
}