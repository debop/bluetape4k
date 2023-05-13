package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

/**
 * Caches and replays some or all items to collectors.
 *
 * NOTE: [kotlinx.coroutines.flow.SharedFlow] 를 사용하는 걸 추천합니다.
 *
 * @see [kotlinx.coroutines.flow.SharedFlow]
 */
class ReplaySubject<T>: AbstractFlow<T>, SubjectApi<T> {

    companion object: KLogging() {
        private val EMPTY = arrayOf<InnerCollector<Any>>()
        private val TERMINATED = arrayOf<InnerCollector<Any>>()
    }

    private val buffer: Buffer<T>

    @Suppress("UNCHECKED_CAST")
    private val collectorsRef = atomic(EMPTY as Array<InnerCollector<T>>)
    private val collectors by collectorsRef

    private var done: Boolean by atomic(false)

    constructor() {
        buffer = UnboundedReplayBuffer()
    }

    constructor(maxSize: Int) {
        buffer = SizeBoundReplayBuffer(maxSize.coerceAtLeast(1))
    }

    constructor(maxTime: Long, unit: TimeUnit): this(Int.MAX_VALUE, maxTime, unit)

    constructor(maxSize: Int, maxTime: Long, unit: TimeUnit):
        this(maxSize, maxTime, unit, { it.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) })

    constructor(maxSize: Int, maxTime: Long, unit: TimeUnit, timeSource: (TimeUnit) -> Long) {
        buffer = TimeAndSizeBoundReplayBuffer(maxSize, maxTime, unit, timeSource)
    }

    override val hasCollectors: Boolean
        get() = collectors.isNotEmpty()

    override val collectorCount: Int
        get() = collectors.size


    override suspend fun collectSafely(collector: FlowCollector<T>) {
        val inner = InnerCollector(collector, this)
        add(inner)
        buffer.replay(inner)
    }

    override suspend fun emit(value: T) {
        if (done) {
            return
        }
        buffer.emit(value)
        collectors.forEach { collector ->
            collector.resume()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun emitError(ex: Throwable?) {
        if (done) {
            return
        }
        done = true
        buffer.error(ex)
        collectorsRef.getAndSet(TERMINATED as Array<InnerCollector<T>>).forEach { collector ->
            collector.resume()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun complete() {
        if (done) {
            return
        }
        done = true
        buffer.complete()
        collectorsRef.getAndSet(TERMINATED as Array<InnerCollector<T>>).forEach { collector ->
            collector.resume()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun add(inner: InnerCollector<T>): Boolean {
        while (true) {
            val a = collectors
            if (areEqualAsAny(a, TERMINATED)) {
                return false
            }
            val n = a.size
            val b = a.copyOf(n + 1)
            b[n] = inner
            if (collectorsRef.compareAndSet(a, b as Array<InnerCollector<T>>)) {
                return true
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun remove(inner: InnerCollector<T>) {
        while (true) {
            val a = collectors
            val n = a.size
            if (n == 0) {
                return
            }

            val j = a.indexOf(inner)
            if (j < 0) {
                return
            }

            var b = EMPTY as Array<InnerCollector<T>?>
            if (n != 1) {
                b = Array(n - 1) { null }
                a.copyInto(b, 0, 0, j)
                a.copyInto(b, j, j + 1)
            }
            if (collectorsRef.compareAndSet(a, b as Array<InnerCollector<T>>)) {
                return
            }
        }
    }


    private interface Buffer<T> {
        fun emit(value: T)
        fun error(e: Throwable?)
        fun complete()
        suspend fun replay(consumer: InnerCollector<T>)
    }

    private class InnerCollector<T>(val consumer: FlowCollector<T>, val parent: ReplaySubject<T>): Resumable() {
        var index: Long = 0L
        var node: Any? = null
    }

    private class UnboundedReplayBuffer<T>: Buffer<T> {

        private companion object: KLogging()

        private val sizeRef = atomic(0L)
        private val size by sizeRef

        private val list = ArrayDeque<T>()

        private var done by atomic(false)
        private var error by atomic<Throwable?>(null)

        override fun emit(value: T) {
            list.add(value)
            sizeRef.incrementAndGet()
        }

        override fun error(e: Throwable?) {
            error = e
            done = true
        }

        override fun complete() {
            done = true
        }

        override suspend fun replay(consumer: InnerCollector<T>) {
            log.debug { "Replay emit ..." }

            while (true) {
                val d = done
                val empty = consumer.index == size
                if (d && empty) {
                    error?.let { throw it }
                    return
                }
                if (!empty) {
                    try {
                        if (coroutineContext.isActive) {
                            consumer.consumer.emit(list[consumer.index.toInt()])
                            consumer.index++
                        } else {
                            throw CancellationException()
                        }
                    } catch (e: Throwable) {
                        consumer.parent.remove(consumer)
                        throw e
                    }
                    continue
                }
                consumer.await()
            }
        }
    }

    private class SizeBoundReplayBuffer<T>(private val maxSize: Int): Buffer<T> {

        private var size: Int = 0

        private var done by atomic(false)
        private var error by atomic<Throwable?>(null)

        @Volatile
        private var head: Node<T>

        @Volatile
        private var tail: Node<T>

        init {
            val h = Node<T>(null)
            tail = h
            head = h
        }

        override fun emit(value: T) {
            val next = Node(value)
            tail.set(next)
            tail = next

            if (size == maxSize) {
                head = head.get()
            } else {
                size++
            }
        }

        override fun error(e: Throwable?) {
            error = e
            done = true
        }

        override fun complete() {
            done = true
        }

        @Suppress("UNCHECKED_CAST")
        override suspend fun replay(consumer: InnerCollector<T>) {
            while (true) {
                val d = done
                var index = consumer.node as? Node<T>
                if (index == null) {
                    index = head
                    consumer.node = index
                }
                val next = index.get()
                val empty = next == null

                if (d && empty) {
                    error?.let { throw it }
                    return
                }
                if (!empty) {
                    try {
                        if (coroutineContext.isActive) {
                            consumer.consumer.emit(next.value!!)
                            consumer.node = next
                        } else {
                            throw CancellationException()
                        }
                    } catch (e: Throwable) {
                        consumer.parent.remove(consumer)
                        throw e
                    }
                    continue
                }
                consumer.await()
            }
        }

        private class Node<T>(val value: T?): AtomicReference<Node<T>>()
    }

    private class TimeAndSizeBoundReplayBuffer<T>(
        private val maxSize: Int,
        private val maxTime: Long,
        private val unit: TimeUnit,
        private val timeSource: (TimeUnit) -> Long,
    ): Buffer<T> {
        private var size: Int = 0

        private var done: Boolean by atomic(false)

        @Volatile
        private var error: Throwable? = null

        @Volatile
        private var head: Node<T>

        @Volatile
        private var tail: Node<T>

        init {
            val h = Node<T>(null, 0L)
            tail = h
            head = h
        }

        override fun emit(value: T) {
            val now = timeSource(unit)
            val next = Node(value, now)
            tail.set(next)
            tail = next

            if (size == maxSize) {
                head = head.get()
            } else {
                size++
            }
            trimTime(now)
        }

        fun trimTime(now: Long) {
            val limit = now - maxTime
            var h = head

            while (true) {
                val next = h.get()
                if (next != null && next.timestamp <= limit) {
                    h = next
                    size--
                } else {
                    break
                }
            }
            head = h
        }

        override fun error(e: Throwable?) {
            error = e
            done = true
        }

        override fun complete() {
            done = true
        }

        fun findHead(): Node<T> {
            val limit = timeSource(unit) - maxTime
            var h = head

            while (true) {
                val next = h.get()
                if (next != null && next.timestamp <= limit) {
                    h = next
                } else {
                    break
                }
            }
            return h
        }

        @Suppress("UNCHECKED_CAST")
        override suspend fun replay(consumer: InnerCollector<T>) {
            while (true) {
                val d = done
                var index = consumer.node as? Node<T>
                if (index == null) {
                    index = findHead()
                    consumer.node = index
                }
                val next = index.get()
                val empty = next == null

                if (d && empty) {
                    error?.let { throw it }
                    return
                }
                if (!empty) {
                    try {
                        if (coroutineContext.isActive) {
                            consumer.consumer.emit(next.value!!)
                            consumer.node = next
                        } else {
                            throw CancellationException()
                        }
                    } catch (e: Throwable) {
                        consumer.parent.remove(consumer)
                        throw e
                    }
                    continue
                }
                consumer.await()
            }
        }

        private class Node<T>(val value: T?, val timestamp: Long): AtomicReference<Node<T>>()
    }
}
