package io.bluetape4k.junit5.coroutines

import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.test.assertFailsWith

class MultiJobTesterTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @Test
    fun `항상 예외를 발생시키는 코드블럭은 실패한다`() = runTest {
        val block: suspend () -> Unit = { fail("foo") }

        runCatching {
            MultiJobTester()
                .add(block)
                .run()
        }.isFailure.shouldBeTrue()
    }

    @Test
    fun `긴 실행 시간을 가진 코드블럭 실행`() = runTest {
        val job = CountingJob()

        MultiJobTester()
            .numJobs(3)
            .roundsPerJob(4)
            .add(job)
            .run()

        yield()
        job.counter.value shouldBeEqualTo 3 * 4
    }

    @Test
    fun `하나의 suspend 함수 실행하기`() = runTest {
        val block = CountingJob()

        MultiJobTester()
            .numJobs(11)
            .roundsPerJob(13)
            .add(block)
            .run()

        block.counter.value shouldBeEqualTo 11 * 13
    }

    @Test
    fun `복수의 suspend 함수 실행하기`() = runTest {
        val block1 = CountingJob()
        val block2 = CountingJob()

        MultiJobTester()
            .numJobs(3)
            .roundsPerJob(1)
            .addAll(block1, block2)
            .run()

        block1.counter.value shouldBeEqualTo 2
        block2.counter.value shouldBeEqualTo 1
    }

    @Test
    fun `numJob 보다 많은 코드블럭을 등록하면 예외가 발생한다`() = runTest {
        val block = CountingJob()

        val mjt = MultiJobTester()
            .numJobs(2)
            .roundsPerJob(1)
            .add(block)
            .add(block)
            .add(block)

        assertFailsWith<IllegalStateException> {
            mjt.run()
        }
    }

    @Test
    fun `실행할 코드블럭이 없으면 예외가 발생한다`() = runTest {
        assertFailsWith<IllegalStateException> {
            MultiJobTester().run()
        }
    }

    private class CountingJob: suspend () -> Unit {
        val counter = atomic(0)

        override suspend fun invoke() {
            delay(3)
            counter.incrementAndGet()
            // log.trace { "count: ${counter.value}" }
        }
    }
}
