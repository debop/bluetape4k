package io.bluetape4k.junit5.concurrency

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeLessOrEqualTo
import org.amshove.kluent.shouldBeTrue
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
    fun `항상 예외를 발생시키는 코드는 항상 실패한다`() {
        val block = { fail("foo") }

        runCatching {
            MultithreadingTester().numThreads(2).roundsPerThread(1).add(block).run()
        }.isFailure.shouldBeTrue()
    }

    @Test
    fun `thread 수가 복수이면 실행시간은 테스트 코드의 실행 시간의 총합보다 작아야 한다`() {
        measureTimeMillis {
            MultithreadingTester().numThreads(2).roundsPerThread(1).add {
                Thread.sleep(100)
            }.add {
                Thread.sleep(100)
            }.run()
        } shouldBeLessOrEqualTo 200
    }

    @Test
    fun `하나의 코드 블럭을 여러번 수행 시 수행 횟수는 같아야 한다`() {
        val block = CountingTestBlock()

        MultithreadingTester().numThreads(11).roundsPerThread(13).add(block).run()

        block.counter.value shouldBeEqualTo 11 * 13
    }

    @Test
    fun `두 개의 코드 블럭을 병렬로 실행`() {
        val block1 = CountingTestBlock()
        val block2 = CountingTestBlock()

        MultithreadingTester().numThreads(3).roundsPerThread(1).addAll(block1, block2).run()

        block1.counter.value shouldBeEqualTo 2
        block2.counter.value shouldBeEqualTo 1
    }

    @Test
    fun `thread 수보다 많은 코드블럭을 등록하면 예외가 발생한다`() {
        val block = CountingTestBlock()

        val mt = MultithreadingTester().numThreads(2).roundsPerThread(1).addAll(block, block, block)

        assertFailsWith<IllegalStateException> {
            mt.run()
        }
    }

    @Test
    fun `실행할 코드 블럭을 등록하지 않으면 예외가 발생한다`() {
        val mt = MultithreadingTester().numThreads(2).roundsPerThread(1)

        assertFailsWith<IllegalStateException> {
            mt.run()
        }
    }

    @Test
    fun `deadlock 이 있는 코드를 실행하면 예외가 발생한다`() {
        try {
            val lock1 = ReentrantLock()
            val latch1 = CountDownLatch(1)
            val lock2 = ReentrantLock()
            val latch2 = CountDownLatch(1)

            MultithreadingTester()
                .numThreads(2)
                .roundsPerThread(1)
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
                }.run()
            fail("RuntimeException expected.")
        } catch (expected: RuntimeException) {
            log.error(expected) { "Expected" }
            expected.message!! shouldContain "Detected 2 deadlocked threads"
        }
    }


    private class CountingTestBlock: () -> Unit {
        companion object: KLogging()

        val counter = atomic(0)

        override fun invoke() {
            counter.incrementAndGet()
            log.trace { "count: ${counter.value}" }
        }
    }
}
