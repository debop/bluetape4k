package io.bluetape4k.junit5.concurrency

import io.bluetape4k.junit5.utils.MultiException
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

/**
 * Multi threading 환경에서 주어진 테스트 코드를 수행할 수 있게 하는 Tester 입니다.
 *
 * ```
 * // 주어진 테스트 코드는 [MultithreadingTester]의 [add] 메소드를 통해 추가할 수 있습니다.
 * MultithreadingTester().numThreads(2).roundsPerThread(1)
 *     .add {
 *         lock1.withLock {
 *             latch2.countDown()
 *             latch1.await()
 *             lock2.withLock {
 *                 fail("Reached unreachable code")
 *             }
 *         }
 *     }
 *     .add {
 *         lock2.withLock {
 *             latch1.countDown()
 *             latch2.await()
 *             lock1.withLock {
 *                 fail("Reached unreachable code")
 *             }
 *         }
 *     }
 *     .run()
 * ```
 */
class MultithreadingTester {

    companion object: KLogging() {
        const val DEFAULT_THREAD_SIZE: Int = 100
        const val DEFAULT_ROUNDS_PER_THREADS: Int = 1000
    }

    private var numThreads = DEFAULT_THREAD_SIZE
    private var roundsPerThreads = DEFAULT_ROUNDS_PER_THREADS
    private val runnables = LinkedList<() -> Unit>()

    private lateinit var monitorThread: Thread
    private lateinit var workerThreads: Array<Thread>
    private val idsOfDeadlockThreads = CopyOnWriteArraySet<Long>()

    fun numThreads(numThreads: Int): MultithreadingTester = apply {
        check(numThreads in 2..2000) { "Invalid numThreads: $numThreads -- must be range in 2..2000" }
        this.numThreads = numThreads
    }

    fun roundsPerThread(roundsPerThreads: Int) = apply {
        check(roundsPerThreads in 1..Int.MAX_VALUE) { "Invalid roundsPerThreads: $roundsPerThreads -- must be range in 1..${Int.MAX_VALUE}" }
        this.roundsPerThreads = roundsPerThreads
    }

    fun add(runnable: () -> Unit) = apply {
        this.runnables.add(runnable)
    }

    fun addAll(vararg runnables: () -> Unit) = apply {
        this.runnables.addAll(runnables)
    }

    fun addAll(runnables: Collection<() -> Unit>) = apply {
        this.runnables.addAll(runnables)
    }


    /**
     * Starts multiple threads, which execute the added [RunnableAssert]s several times.
     * This method blocks until all started threads are finished.
     *
     * @see [numThreads]
     * @see [numRoundsPerThread]
     */
    fun run() {
        check(runnables.isNotEmpty()) {
            "No RunnableAsserts added. Please add at least one RunnableAssert."
        }
        check(numThreads >= runnables.size) {
            "numThreads($numThreads) < runnableAsserts.size(${runnables.size})"
        }

        val me = MultiException()
        startMonitorThread(me)
        try {
            startWorkerThreads(me)
            joinWorkerThreads()
        } finally {
            stopMonitorThread()
        }
        me.throwIfNotEmpty()
    }

    private fun startMonitorThread(me: MultiException) {
        log.debug { "Start monitor thread ..." }

        val threadMXBean = ManagementFactory.getThreadMXBean()
        val knownDeadlockedThreadIds = threadMXBean.findDeadlockedThreads()?.toSet() ?: emptySet()

        monitorThread = object: Thread("MultithreadingTester-monitor") {
            override fun run() {
                try {
                    while (!isInterrupted) {
                        val threadIds = threadMXBean.findDeadlockedThreads()
                        if (threadIds != null) {
                            val newDeadlockThreads = threadIds.toMutableSet()
                            newDeadlockThreads.removeAll(knownDeadlockedThreadIds)
                            if (newDeadlockThreads.isNotEmpty()) {
                                idsOfDeadlockThreads.addAll(newDeadlockThreads)
                                val errorMessage = threadMXBean.getDeadlockThreadInfo(threadIds)
                                me.add(RuntimeException(errorMessage))
                                return // deadlock thread가 생겼으므로 monitor thread를 중단한다
                            }
                        }
                        Thread.sleep(1000)
                    }
                } catch (expected: InterruptedException) {
                    // Nothing to do
                } catch (unexpected: Throwable) {
                    me.add(unexpected)
                }
            }
        }
        monitorThread.priority = Thread.MAX_PRIORITY
        monitorThread.start()
    }

    private fun ThreadMXBean.getDeadlockThreadInfo(threadIds: LongArray): String = buildString {
        append("Detected ").append(threadIds.size).append(" deadlocked threads:")
        getThreadInfo(threadIds, true, true)
            .forEach {
                append("\n\t").append(it)
            }
    }

    private fun startWorkerThreads(me: MultiException) {
        log.debug { "Start worker threads ... numThreads=$numThreads" }

        var iter = runnables.iterator()
        val latch = CountDownLatch(numThreads)

        workerThreads = Array(numThreads) {
            // thread 수만큼 runnableAssert를 반복해서 사용한다
            if (!iter.hasNext()) {
                iter = runnables.iterator()
            }
            val runnableAssert = iter.next()
            thread(start = true, name = "MultithreadingTester-worker-$it") {
                try {
                    latch.countDown()
                    latch.await()
                    repeat(roundsPerThreads) {
                        runnableAssert.invoke()
                    }
                } catch (t: Throwable) {
                    me.add(t)
                }
            }
        }
    }

    private fun joinWorkerThreads() {
        log.debug { "Join worker threads ..." }

        var foundAliveWorkerThread: Boolean
        do {
            foundAliveWorkerThread = false
            repeat(numThreads) {
                try {
                    val workerThread = workerThreads[it]
                    workerThread.join(100)
                    if (workerThread.isAlive && !idsOfDeadlockThreads.contains(workerThread.id)) {
                        foundAliveWorkerThread = true
                    }
                } catch (e: InterruptedException) {
                    workerThreads.forEach { it.interrupt() }
                    Thread.currentThread().interrupt()
                    throw RuntimeException("Get interrupted", e)
                }
            }
        } while (foundAliveWorkerThread && monitorThread.isAlive)
    }

    private fun stopMonitorThread() {
        log.debug { "Stop monitor thread ..." }
        monitorThread.interrupt()
        try {
            monitorThread.join()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("Get interrupted", e)
        }
    }

}
