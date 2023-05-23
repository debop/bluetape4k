package io.bluetape4k.coroutines.chains


interface BufferChain<out T>: Chain<T> {
    suspend fun nextList(size: Int): List<T>
    override suspend fun fork(): BufferChain<T>
}

/**
 * A chain with blocking generator that could be used without suspension
 */
interface BlockingChain<out T>: Chain<T> {
    /**
     * Get the next value without concurrency support. Not guaranteed to be thread safe.
     */
    fun nextBlocking(): T
    override suspend fun next(): T = nextBlocking()
    override suspend fun fork(): BlockingChain<T>
}

interface BlockingBufferChain<out T>: BlockingChain<T>, BufferChain<T> {

    fun nextBufferBlocking(size: Int): List<T>

    override fun nextBlocking(): T = nextBufferBlocking(1).first()

    override suspend fun nextList(size: Int): List<T> = nextBufferBlocking(size)

    override suspend fun fork(): BlockingBufferChain<T>
}

suspend inline fun <reified T: Any> Chain<T>.nextList(size: Int): List<T> {
    return when (this) {
        is BufferChain -> nextList(size)
        else -> List(size) { next() }
    }
}

inline fun <reified T: Any> BlockingChain<T>.nextBufferBlocking(size: Int): List<T> {
    return when (this) {
        is BlockingBufferChain -> nextBufferBlocking(size)
        else -> List(size) { nextBlocking() }
    }
}
