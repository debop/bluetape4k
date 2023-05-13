package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

/**
 * 하나의 Item을 캐시했다가 새로운 collectors 에게 replay 합니다.
 */
class BehaviorSubject<T> private constructor(
    @Volatile private var current: Node<T>
): AbstractFlow<T>(), SubjectApi<T> {

    companion object: KLogging() {
        private val EMPTY = arrayOf<InnerCollector>()
        private val TERMINATED = arrayOf<InnerCollector>()
        private val NONE = Any()
        private val DONE = Node(NONE)

        @Suppress("UNCHECKED_CAST")
        operator fun <T: Any> invoke(initialValue: T = NONE as T): BehaviorSubject<T> {
            return BehaviorSubject(Node(initialValue))
        }
    }

    private val collectors = atomic(EMPTY)
    private var error: Throwable? = null

    val value: T get() = valueOrNull ?: error("No value")

    val valueOrNull: T?
        get() {
            val currentValue = current.value
            return if (currentValue == NONE) null else currentValue
        }

    override val hasCollectors: Boolean get() = collectors.value.isNotEmpty()
    override val collectorCount: Int get() = collectors.value.size


    override suspend fun emit(value: T) {
        if (current == DONE)
            return

        val next = Node(value)
        current.set(next)
        current = next

        collectors.value.forEach { innerCollector ->
            try {
                innerCollector.consumeReady.await()
                innerCollector.resume()
            } catch (e: CancellationException) {
                remove(innerCollector)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun emitError(ex: Throwable?) {
        if (current == DONE)
            return

        error = ex
        current.set(DONE as Node<T>)
        current = DONE

        collectors.getAndSet(TERMINATED).forEach { innerCollector ->
            runCatching {
                innerCollector.consumeReady.await()
                innerCollector.resume()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun complete() {
        if (current == DONE)
            return

        current.set(DONE as Node<T>)
        current = DONE

        collectors.getAndSet(TERMINATED).forEach { innerCollector ->
            runCatching {
                innerCollector.consumeReady.await()
                innerCollector.resume()
            }
        }
    }


    override suspend fun collectSafely(collector: FlowCollector<T>) {
        val inner = InnerCollector()

        suspend fun tryEmit(isActive: Boolean, value: T) {
            try {
                if (isActive) {
                    collector.emit(value)
                } else {
                    throw CancellationException()
                }
            } catch (e: Throwable) {
                remove(inner)
                inner.consumeReady.resume()
                throw e
            }
        }

        if (add(inner)) {
            var curr = current
            if (curr.value != NONE) {
                tryEmit(coroutineContext.isActive, curr.value)
            }

            while (true) {
                inner.consumeReady.resume()
                inner.await()

                val next = curr.get()

                if (next == DONE) {
                    val ex = error
                    ex?.let { throw it }
                    return
                }

                tryEmit(coroutineContext.isActive, next.value)

                curr = next
            }
        }

        error?.let { throw it }
    }

    @Suppress("UNCHECKED_CAST")
    private fun add(inner: InnerCollector): Boolean {
        while (true) {
            val a = collectors.value
            if (areEqualAsAny(a, TERMINATED)) {
                return false
            }
            val n = a.size
            val b = a.copyOf(n + 1)
            b[n] = inner
            if (collectors.compareAndSet(a, b as Array<InnerCollector>)) {
                return true
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun remove(inner: InnerCollector) {
        while (true) {
            val a = collectors.value
            val n = a.size
            if (n == 0) {
                return
            }

            val j = a.indexOf(inner)
            if (j < 0) {
                return
            }

            var b = EMPTY as Array<InnerCollector?>
            if (n != 1) {
                b = Array(n - 1) { null }
                a.copyInto(b, 0, 0, j)
                a.copyInto(b, j, j + 1)
            }
            if (collectors.compareAndSet(a, b as Array<InnerCollector>)) {
                return
            }
        }
    }


    private class InnerCollector: Resumable() {
        val consumeReady = Resumable()
    }

    private class Node<T>(val value: T): AtomicReference<Node<T>>()
}
