package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.coroutines.flow.exception.FlowException
import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.coroutines.flow.extensions.ResumableCollector
import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlin.coroutines.cancellation.CancellationException

/**
 * A subject implementation that awaits a certain number of collectors
 * to start consuming, then allows the producer side to deliver items
 * to them.
 *
 * @param <T> the element type of the [Flow]
 * @param expectedCollectors the number of items to buffer until consumers arrive
 */
class MulticastSubject<T> private constructor(
    expectedCollectors: Int,
): AbstractFlow<T>(), SubjectApi<T> {

    companion object: KLogging() {
        private val EMPTY = arrayOf<ResumableCollector<Any>>()
        private val TERMINATED = arrayOf<ResumableCollector<Any>>()
        private val DONE = FlowException("Subject completed")

        operator fun <T> invoke(expectedCollectors: Int): MulticastSubject<T> {
            return MulticastSubject(expectedCollectors.coerceAtLeast(1))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val collectorsRef = atomic(EMPTY as Array<ResumableCollector<T>>)
    private val collectors by collectorsRef

    private val producer = Resumable()

    private val _remainingCollectors = atomic(expectedCollectors)
    private val remainingCollectors by _remainingCollectors

    private var terminated: Throwable? = null

    override val hasCollectors: Boolean
        get() = collectors.isNotEmpty()

    override val collectorCount: Int
        get() = collectors.size

    override suspend fun emit(value: T) {
        awaitCollectors()
        collectors.forEach { collector ->
            try {
                collector.next(value)
            } catch (e: CancellationException) {
                remove(collector)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun emitError(ex: Throwable?) {
        terminated = ex
        collectorsRef.getAndSet(TERMINATED as Array<ResumableCollector<T>>).forEach { collector ->
            try {
                collector.error(ex)
            } catch (_: CancellationException) {
                // ignored at this point
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun complete() {
        terminated = DONE
        collectorsRef.getAndSet(TERMINATED as Array<ResumableCollector<T>>).forEach { collector ->
            try {
                collector.complete()
            } catch (_: CancellationException) {
                // ignored at this point
            }
        }
    }

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        val rc = ResumableCollector<T>()
        if (add(rc)) {
            while (true) {
                val a = remainingCollectors
                if (a == 0) {
                    break
                }
                if (_remainingCollectors.compareAndSet(a, a - 1)) {
                    if (a == 1) {
                        producer.resume()
                    }
                    break
                }
            }
            rc.drain(collector) { remove(it) }
        } else {
            val ex = terminated
            if (ex != null && ex != DONE) {
                throw ex
            }
        }
    }

    private suspend fun awaitCollectors() {
        if (remainingCollectors > 0) {
            producer.await()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun add(inner: ResumableCollector<T>): Boolean {
        while (true) {
            val array = collectors
            if (areEqualAsAny(array, TERMINATED)) {
                return false
            }
            val n = array.size
            val b = array.copyOf(n + 1)
            b[n] = inner
            if (collectorsRef.compareAndSet(array, b as Array<ResumableCollector<T>>)) {
                return true
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun remove(inner: ResumableCollector<T>) {
        while (true) {
            val array = collectors
            val n = array.size
            if (n == 0) {
                return
            }

            val j = array.indexOf(inner)
            if (j < 0) {
                return
            }

            var b = EMPTY as Array<ResumableCollector<T>?>
            if (n != 1) {
                b = Array(n - 1) { null }
                array.copyInto(b, 0, 0, j)
                array.copyInto(b, j, j + 1)
            }
            if (collectorsRef.compareAndSet(array, b as Array<ResumableCollector<T>>)) {
                return
            }
        }
    }
}
