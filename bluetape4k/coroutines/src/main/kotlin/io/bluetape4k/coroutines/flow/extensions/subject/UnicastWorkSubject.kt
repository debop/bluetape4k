package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.coroutines.flow.exception.FlowNoElementException
import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.FlowCollector
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Collector가 수집하기 전까지 요소를 버퍼링합니다.
 * 반복적으로 collect를 수행할 수 있습니다.
 */
class UnicastWorkSubject<T>: AbstractFlow<T>(), SubjectApi<T> {

    companion object: KLogging() {
        val terminated = FlowNoElementException("No more elements")
    }

    val resumable = Resumable()
    private val queue = ConcurrentLinkedQueue<T>()

    private var terminal by atomic<Throwable?>(null)
    private val currentRef = atomic<FlowCollector<T>?>(null)
    private val current by currentRef

    override val hasCollectors: Boolean
        get() = current != null

    override val collectorCount: Int
        get() = if (hasCollectors) 1 else 0

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        while (true) {
            val curr = current
            if (curr != null) {
                error("Only one collector allowed.")
            }
            if (currentRef.compareAndSet(curr, collector)) {
                break
            }
        }

        while (true) {
            val t = terminal
            val v = queue.poll()

            // 종료되었거나 요소가 없을 때
            if (t != null && v == null) {
                currentRef.getAndSet(null)
                if (t != terminated) {
                    throw t
                }
                return
            }
            if (v != null) {
                try {
                    collector.emit(v)
                } catch (e: Throwable) {
                    currentRef.getAndSet(null)
                    throw e
                }
            } else {
                resumable.await()
            }
        }
    }

    override suspend fun emit(value: T) {
        queue.offer(value)
        resumable.resume()
    }


    override suspend fun emitError(ex: Throwable?) {
        terminal = ex
        resumable.resume()
    }

    override suspend fun complete() {
        terminal = terminated
        resumable.resume()
    }
}
