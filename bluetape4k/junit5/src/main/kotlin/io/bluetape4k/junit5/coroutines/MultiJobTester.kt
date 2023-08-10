package io.bluetape4k.junit5.coroutines

import io.bluetape4k.junit5.utils.MultiException
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext

/**
 * suspend 함수를 제한된 스레드 수에서 동시에 실행시키고, 모든 suspend 함수가 종료되기를 기다린다.
 *
 * ```
 * val block1 = CountingSuspendBlock()
 * val block2 = CountingSuspendBlock()
 *
 * // block1은 2번, block2는 1번 실행된다.
 * MultiJobTester()
 *     .numThreads(3)
 *     .roundsPerThread(1)
 *     .addAll(block1, block2)
 *     .run()
 *
 * block1.count shouldBeEqualTo 2
 * block2.count shouldBeEqualTo 1
 * ```
 */
@Suppress("OPT_IN_USAGE")
class MultiJobTester {

    companion object: KLogging() {
        const val DEFAULT_NUM_JOBS: Int = 64
        const val DEFAULT_ROUNDS_PER_THREADS: Int = 10
    }

    private var numThreads = DEFAULT_NUM_JOBS
    private var roundsPerThreads = DEFAULT_ROUNDS_PER_THREADS
    private val suspendBlocks = mutableListOf<suspend () -> Unit>()

    // Coroutines 에서는 사용하지 않는다.
    // private lateinit var monitorThread: Thread
    // private val idsOfDeadlockThreads = CopyOnWriteArraySet<Long>()

    private lateinit var workerDispatcher: ExecutorCoroutineDispatcher
    private lateinit var workerJobs: List<Job>

    fun numJobs(numThreads: Int): MultiJobTester = apply {
        check(numThreads in 2..2000) { "Invalid numThreads: $numThreads -- must be range in 2..2000" }
        this.numThreads = numThreads
    }

    fun roundsPerJob(roundsPerThreads: Int) = apply {
        check(roundsPerThreads in 1..Int.MAX_VALUE) {
            "Invalid roundsPerThreads: $roundsPerThreads -- must be range in 1..${Int.MAX_VALUE}"
        }
        this.roundsPerThreads = roundsPerThreads
    }

    fun add(block: suspend () -> Unit) = apply {
        this.suspendBlocks.add(block)
    }

    fun addAll(vararg blocks: suspend () -> Unit) = apply {
        this.suspendBlocks.addAll(blocks)
    }

    fun addAll(blocks: Collection<suspend () -> Unit>) = apply {
        this.suspendBlocks.addAll(blocks)
    }

    suspend fun run() {
        check(suspendBlocks.isNotEmpty()) { "No suspend blocks to run" }
        check(numThreads >= suspendBlocks.size) {
            "numThreads($numThreads) must be greater than suspendBlocks.size(${suspendBlocks.size})"
        }

        val me = MultiException()

        startWorkerJobs(me)
        awaitWorkerJobs()

        me.throwIfNotEmpty()
    }

    private suspend fun startWorkerJobs(me: MultiException) = coroutineScope {
        log.debug { "Start worker jobs ..." }

        var iter = suspendBlocks.iterator()

        workerDispatcher = newFixedThreadPoolContext(numThreads, "coroutine-tester")
        workerJobs = List(numThreads) {
            if (!iter.hasNext()) {
                iter = suspendBlocks.iterator()
            }
            val block = iter.next()
            launch(workerDispatcher) {
                repeat(roundsPerThreads) {
                    runCatching {
                        block()
                    }.onFailure {
                        me.add(it)
                    }
                }
            }
        }
    }

    private suspend fun awaitWorkerJobs() {
        log.debug { "Await worker jobs ..." }
        workerJobs.joinAll()
        runCatching { workerDispatcher.close() }
    }
}
