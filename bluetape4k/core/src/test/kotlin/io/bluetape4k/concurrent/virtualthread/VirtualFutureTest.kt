package io.bluetape4k.concurrent.virtualthread

import io.bluetape4k.junit5.concurrency.VirtualthreadTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class VirtualFutureTest {

    companion object: KLogging()

    @Test
    fun `run task with virtual thread`() {
        val vfuture = VirtualFuture.async<Int> {
            Thread.sleep(1000)
            1
        }

        vfuture.await() shouldBeEqualTo 1
    }

    @Test
    fun `run tasks with virtual threads`() {
        val taskSize = 100

        val tasks = List(taskSize) {
            {
                log.debug { "Run task[$it]" }
                Thread.sleep(1000)
                it
            }
        }
        val vfutures = VirtualFuture.asyncAll(tasks)
        vfutures.await() shouldBeEqualTo (0 until taskSize).toList()
    }

    @Test
    fun `run tasks with virtual thread tester`() {
        val taskNumber = atomic(0)

        // 1초씩 대기하는 1000 개의 작업을 Virtual Thread를 이용하면, 1초만에 모든 작업이 완료됩니다.
        VirtualthreadTester()
            .numThreads(1000)
            .roundsPerThread(1)
            .add {
                Thread.sleep(1000)
                log.debug { "Run task ...${taskNumber.incrementAndGet()}" }
            }
            .run()
    }
}
