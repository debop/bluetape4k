package io.bluetape4k.junit5.coroutines

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeLessOrEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.system.measureTimeMillis
import kotlin.test.assertFailsWith

class MultiJobTesterTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @Test
    fun `항상 예외를 발생시키는 코드는 실패해야 한다`() = runTest {
        val block: suspend () -> Unit = { fail("foo") }

        runCatching {
            MultiJobTester().add(block).run()
        }.isSuccess.shouldBeFalse()
    }

    @Test
    fun `긴 실행 시간을 가지는 코드블럭 실행`() = runSuspendTest {
        measureTimeMillis {
            MultiJobTester()
                .numThreads(2)
                .roundsPerThread(1)
                .add {
                    log.trace { "Run suspend block" }
                    delay(1000)
                }
                .run()
        } shouldBeLessOrEqualTo 2000
    }

    @Test
    fun `하나의 suspend 함수 실행하기`() = runTest {
        val block = CountingSuspendBlock()

        MultiJobTester()
            .numThreads(11)
            .roundsPerThread(13)
            .add(block)
            .run()

        block.count shouldBeEqualTo 11 * 13
    }

    @Test
    fun `복수의 suspend 함수 실행하기`() = runTest {
        val block1 = CountingSuspendBlock()
        val block2 = CountingSuspendBlock()
        MultiJobTester()
            .numThreads(3)
            .roundsPerThread(1)
            .addAll(block1, block2)
            .run()

        block1.count shouldBeEqualTo 2
        block2.count shouldBeEqualTo 1
    }

    @Test
    fun `numThread 보다 많은 runnable을 등록하면 예외가 발생한다`() = runTest {
        val block = CountingSuspendBlock()

        assertFailsWith<IllegalStateException> {
            MultiJobTester()
                .numThreads(2)
                .roundsPerThread(1)
                .add(block)
                .add(block)
                .add(block)
                .run()
        }
    }

    @Test
    fun `실행할 block 이 없으면 예외가 발생한다`() = runTest {
        assertFailsWith<IllegalStateException> {
            MultiJobTester().run()
        }
    }

    private inner class CountingSuspendBlock: suspend () -> Unit {
        val counter = atomic(0)

        val count by counter

        override suspend fun invoke() {
            counter.incrementAndGet()
        }
    }
}
