package io.bluetape4k.coroutines

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Suppress("UNCHECKED_CAST")
class RingBuffer<T>(
    private val buffer: MutableList<T?>,
    private var startIndex: Int = 0,
    size: Int = 0,
): Iterable<T?> by buffer {

    companion object: KLogging() {
        operator fun <T: Any> invoke(size: Int, empty: T): RingBuffer<T> {
            val buffer = MutableList(size) { empty } as MutableList<T?>
            return RingBuffer(buffer)
        }

        fun <T> boxing(size: Int): RingBuffer<T> {
            val buffer: MutableList<T?> = MutableList(size) { null }
            return RingBuffer(buffer)
        }
    }

    private val mutex: Mutex = Mutex()

    var size: Int = size
        private set

    val isFull: Boolean get() = size == buffer.size

    operator fun get(index: Int): T {
        require(index >= 0) { "Index must be positive" }
        require(index < size) { "Index $index is out of circular buffer size $size" }
        return buffer[startIndex.forward(index)] as T
    }

    override fun iterator(): Iterator<T> {
        return object: AbstractIterator<T>() {
            private var count = size
            private var index = startIndex
            val copy = buffer.toList()

            override fun computeNext() {
                if (count == 0) done() else {
                    setNext(copy[index] as T)
                    index = index.forward(1)
                    count--
                }
            }
        }
    }

    suspend fun snapshot(): List<T> = mutex.withLock {
        val copy = buffer.toList()
        List(size) { i -> copy[startIndex.forward(i)] as T }
    }

    suspend fun push(element: T) {
        mutex.withLock {
            buffer[startIndex.forward(size)] = element
            if (isFull) startIndex++ else size++
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Int.forward(n: Int): Int = (this + n) % buffer.size

    override fun toString(): String {
        return buffer.joinToString(prefix = "[", separator = ", ", postfix = "]")
    }
}
