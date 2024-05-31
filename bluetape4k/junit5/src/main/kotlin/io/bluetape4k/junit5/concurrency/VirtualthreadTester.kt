package io.bluetape4k.junit5.concurrency

import io.bluetape4k.junit5.utils.MultiException
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

class VirtualthreadTester {

    companion object: KLogging() {
        const val DEFAULT_THREAD_SIZE: Int = 100
        const val DEFAULT_ROUNDS_PER_THREAD: Int = 1000

        const val MIN_THREAD_SIZE: Int = 2
        const val MAX_THREAD_SIZE: Int = 2000
        const val MAX_ROUNDS_PER_THREAD: Int = Int.MAX_VALUE
    }

    private var numThreads = DEFAULT_THREAD_SIZE
    private var roundsPerThread = DEFAULT_ROUNDS_PER_THREAD
    private val runnables = LinkedList<() -> Unit>()

    private val executor = Executors.newVirtualThreadPerTaskExecutor()
    private lateinit var futures: List<Future<Unit>>

    fun numThreads(value: Int) = apply {
        check(value in MIN_THREAD_SIZE..MAX_THREAD_SIZE) {
            "Invalid numThreads: $value -- must be range in $MIN_THREAD_SIZE..$MAX_THREAD_SIZE"
        }
        this.numThreads = value.coerceIn(MIN_THREAD_SIZE, MAX_THREAD_SIZE)
    }

    fun roundsPerThread(value: Int) = apply {
        check(value in 1..MAX_ROUNDS_PER_THREAD) {
            "Invalid roundsPerThread: $value -- must be range in 1..$MAX_ROUNDS_PER_THREAD"
        }
        this.roundsPerThread = value.coerceIn(1, MAX_ROUNDS_PER_THREAD)
    }

    fun add(testBlock: () -> Unit) = apply {
        this.runnables.add(testBlock)
    }

    fun addAll(vararg testBlocks: () -> Unit) = apply {
        this.runnables.addAll(testBlocks)
    }

    fun addAll(testBlocks: Collection<() -> Unit>) = apply {
        this.runnables.addAll(testBlocks)
    }

    fun run() {
        check(runnables.isNotEmpty()) {
            "No test blocks added. Please add test blocks using add() method."
        }
        check(numThreads >= runnables.size) {
            "Number of threads[$numThreads] must be greater than or equal to the number of test blocks[${runnables.size}]."
        }

        val me = MultiException()

        try {
            startWorkerThreads(me)
            joinWorkerThreads()
        } finally {
            me.throwIfNotEmpty()
        }
    }

    private fun startWorkerThreads(me: MultiException) {
        log.trace { "Start virtual threads ... numThreads=$numThreads" }

        val tasks = List(numThreads * roundsPerThread) { index ->
            Callable {
                try {
                    val runnableAssert = runnables[index % runnables.size]
                    runnableAssert.invoke()
                } catch (t: Throwable) {
                    me.add(t)
                }
            }
        }

        futures = executor.invokeAll(tasks.map { it })
    }

    private fun joinWorkerThreads() {
        log.trace { "Join worker threads ..." }
        if (this::futures.isInitialized) {
            futures.map { it.get() }
        }
    }
}
