package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.coroutines.flow.exception.FlowNoElementException
import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.FlowCollector
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Single collector 가 collect 하기 전까지는 버퍼링을 합니다.
 *
 * **collector 가 완료되면, 재작업을 수행할 수 없습니다.**
 */
class UnicastSubject<T>: AbstractFlow<T>(), SubjectApi<T> {

    companion object: KLogging() {
        val terminatedCollector = FlowCollector<Any?> {
            log.trace { "TerminatedCollector was called." }
        }
        val terminated = FlowNoElementException("No more elements")
    }

    private val queue = ConcurrentLinkedQueue<T>()

    private val terminalRef = atomic<Throwable?>(null)
    val terminal by terminalRef

    val resumable = Resumable()

    private val currentRef = atomic<FlowCollector<T>?>(null)
    val current by currentRef

    val collectorCancelled: Boolean
        get() = current == terminatedCollector

    override val hasCollectors: Boolean
        get() = current != null && current != terminatedCollector

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
                currentRef.getAndSet(terminatedCollector as FlowCollector<T>)
                if (t != terminated) {
                    throw t
                }
                return
            }
            if (v != null) {
                try {
                    collector.emit(v)
                } catch (e: Throwable) {
                    currentRef.getAndSet(terminatedCollector as FlowCollector<T>)
                    queue.clear()
                    throw e
                }
            } else {
                resumable.await()
            }
        }
    }

    override suspend fun emit(value: T) {
        if (current != terminatedCollector) {
            queue.offer(value)
            resumable.resume()
        } else {
            queue.clear()
        }
    }

    override suspend fun emitError(ex: Throwable?) {
        terminalRef.value = ex
        resumable.resume()
    }

    override suspend fun complete() {
        terminalRef.value = terminated
        resumable.resume()
    }
}
