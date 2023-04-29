package io.bluetape4k.junit5.concurrency

import io.bluetape4k.junit5.utils.MultiException
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

/**
 * Multi threading 환경에서 주어진 테스트 코드를 수행할 수 있게 하는 Tester 입니다.
 */
class MultithreadingTester {

    companion object: KLogging() {
        const val DEFAULT_THREAD_SIZE: Int = 100
        const val DEFAULT_ROUNDS_PER_THREADS: Int = 1000

        private fun convertToRunnableAssert(runnable: () -> Unit): RunnableAssert {
            return when (runnable) {
                is RunnableAssert -> runnable
                else -> object: RunnableAssert(runnable.toString()) {
                    override fun run() {
                        runnable.invoke()
                    }
                }
            }
        }
    }

    private var numThreads = DEFAULT_THREAD_SIZE
    private var roundsPerThreads = DEFAULT_ROUNDS_PER_THREADS
    private val runnableAsserts = mutableListOf<RunnableAssert>()

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

    fun add(runnableAssert: RunnableAssert) = apply {
        this.runnableAsserts.add(runnableAssert)
    }

    fun add(runnable: () -> Unit) = apply {
        this.runnableAsserts.add(convertToRunnableAssert(runnable))
    }

    fun addAll(vararg runnableAsserts: RunnableAssert) = apply {
        this.runnableAsserts.addAll(runnableAsserts)
    }

    fun addAll(vararg runnables: () -> Unit) = apply {
        this.runnableAsserts.addAll(runnables.map { convertToRunnableAssert(it) })
    }

    fun addAll(runnables: Collection<() -> Unit>) = apply {
        this.runnableAsserts.addAll(runnables.map { convertToRunnableAssert(it) })
    }


    /**
     * Starts multiple threads, which execute the added [RunnableAssert]s several times.
     * This method blocks until all started threads are finished.
     *
     * @see [numThreads]
     * @see [numRoundsPerThread]
     */
    fun run() {
        check(runnableAsserts.isNotEmpty()) {
            "No RunnableAsserts added. Please add at least one RunnableAssert."
        }
        check(numThreads >= runnableAsserts.size) {
            "numThreads($numThreads) < runnableAsserts.size(${runnableAsserts.size})"
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

        var iter = runnableAsserts.iterator()
        val latch = CountDownLatch(numThreads)

        workerThreads = Array(numThreads) {
            // thread 수만큼 runnableAssert를 반복해서 사용한다
            if (!iter.hasNext()) {
                iter = runnableAsserts.iterator()
            }
            val runnableAssert = iter.next()
            thread(start = true, name = "MultithreadingTester-worker-$it") {
                try {
                    latch.countDown()
                    latch.await()
                    repeat(roundsPerThreads) {
                        runnableAssert.run()
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
