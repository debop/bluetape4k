package io.bluetape4k.junit5.coroutines

import io.bluetape4k.junit5.utils.MultiException
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.yield

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
class MultiJobTester {

    companion object: KLogging() {
        const val DEFAULT_NUM_JOBS: Int = 64
        const val MIN_NUM_JOBS: Int = 2
        const val MAX_NUM_JOBS: Int = 1000

        const val DEFAULT_ROUNDS_PER_JOB: Int = 10
        const val MIN_ROUNDS_PER_JOB: Int = 1
        const val MAX_ROUNDS_PER_JOB: Int = Int.MAX_VALUE
    }

    private var numJobs = DEFAULT_NUM_JOBS
    private var roundsPerJob = DEFAULT_ROUNDS_PER_JOB

    private val suspendBlocks = mutableListOf<suspend () -> Unit>()

    private lateinit var workerDispather: ExecutorCoroutineDispatcher
    private lateinit var workerJobs: List<Job>

    fun numJobs(value: Int) = apply {
        check(value in MIN_NUM_JOBS..MAX_NUM_JOBS) {
            "Invalid numJobs: $value -- must be range in $MIN_NUM_JOBS..$MAX_NUM_JOBS"
        }
        this.numJobs = value
    }

    fun roundsPerJob(value: Int) = apply {
        check(value in MIN_ROUNDS_PER_JOB..MAX_ROUNDS_PER_JOB) {
            "Invalid roundsPerJob: $value -- must be range in $MIN_ROUNDS_PER_JOB..$MAX_ROUNDS_PER_JOB"
        }
        this.roundsPerJob = value
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
        check(numJobs >= suspendBlocks.size) {
            "numJobs[$numJobs] must be greater than or equal to the number of suspend blocks[${suspendBlocks.size}]"
        }

        val me = MultiException()
        try {
            startWorkerJobs(me)
            yield()
            awaitWorkerJobs()
        } finally {
            me.throwIfNotEmpty()
        }
    }

    private suspend fun startWorkerJobs(me: MultiException): Unit = coroutineScope {
        log.trace { "Start multi job testing ..." }

        val sequence = atomic(0)

        workerDispather = newFixedThreadPoolContext(numJobs, "multi-job-tester")

        workerJobs = List(numJobs * roundsPerJob) {
            val block = suspendBlocks[sequence.getAndIncrement() % suspendBlocks.size]
            launch(workerDispather) {
                try {
                    block()
                } catch (e: Throwable) {
                    me.add(e)
                }
            }
        }
    }

    private suspend fun awaitWorkerJobs() {
        log.trace { "Await multi testing jobs..." }
        runCatching { workerJobs.joinAll() }
        runCatching { workerDispather.close() }
    }
}
