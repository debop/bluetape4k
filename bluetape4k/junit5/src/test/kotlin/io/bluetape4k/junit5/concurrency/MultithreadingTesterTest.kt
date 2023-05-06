package io.bluetape4k.junit5.concurrency

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeLessOrEqualTo
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.system.measureTimeMillis
import kotlin.test.assertFailsWith

class MultithreadingTesterTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @Test
    fun `항상 예외를 발생시키는 코드 블럭은 항상 실패한다`() {
        val ra = { fail("foo") }

        runCatching {
            MultithreadingTester().add(ra).run()
        }
            .isSuccess.shouldBeFalse()
    }

    @Test
    fun `긴 실행 시간을 가지는 코드블럭 실행`() {
        measureTimeMillis {
            MultithreadingTester()
                .numThreads(2)
                .roundsPerThread(1)
                .add {
                    Thread.sleep(1000)
                }
                .run()
        } shouldBeLessOrEqualTo 2000
    }

    @Test
    fun `하나의 RunnableAssert 실행`() {
        val ra1 = CountingRunnableAssert()
        MultithreadingTester()
            .numThreads(11)
            .roundsPerThread(13)
            .add(ra1)
            .run()

        ra1.count shouldBeEqualTo 11 * 13
    }

    @Test
    fun `두개의 RunnableAssert 실행`() {
        val ra1 = CountingRunnableAssert()
        val ra2 = CountingRunnableAssert()

        MultithreadingTester()
            .numThreads(3)
            .roundsPerThread(1)
            .addAll(ra1, ra2)
            .run()

        ra1.count shouldBeEqualTo 2
        ra2.count shouldBeEqualTo 1
    }

    @Test
    fun `numThreads 보다 많은 runnable을 등록하면 예외가 발생한다`() {
        val ra = CountingRunnableAssert()
        val mt = MultithreadingTester()
            .numThreads(2)
            .roundsPerThread(1)
            .add(ra)
            .add(ra)
            .add(ra)

        assertFailsWith<IllegalStateException> {
            mt.run()
        }
    }

    @Test
    fun `runnable을 비어있으면 예외가 발생한다`() {
        val mt = MultithreadingTester()
            .numThreads(2)
            .roundsPerThread(1)

        assertFailsWith<IllegalStateException> {
            mt.run()
        }
    }

    @Test
    fun `deadlock 이 있는 코드 실행`() {
        try {
            val lock1 = ReentrantLock()
            val latch1 = CountDownLatch(1)
            val lock2 = ReentrantLock()
            val latch2 = CountDownLatch(1)

            MultithreadingTester().numThreads(2).roundsPerThread(1)
                .add {
                    lock1.withLock {
                        latch2.countDown()
                        latch1.await()
                        lock2.withLock {
                            fail("Reached unreachable code")
                        }
                    }
                }
                .add {
                    lock2.withLock {
                        latch1.countDown()
                        latch2.await()
                        lock1.withLock {
                            fail("Reached unreachable code")
                        }
                    }
                }
                .run()
            fail("RuntomeException expected.")
        } catch (expected: RuntimeException) {
            log.error(expected) { "expected" }
            expected.message!! shouldContain "Detected 2 deadlocked threads"
        }
    }

    private class CountingRunnableAssert: () -> Unit { // RunnableAssert("CountingRunnableAssert") {

        companion object: KLogging()

        val counter: AtomicInt = atomic(0)
        val count: Int by counter

        override fun invoke() {
            counter.incrementAndGet()
            log.trace { "count: $count" }
        }
    }
}
