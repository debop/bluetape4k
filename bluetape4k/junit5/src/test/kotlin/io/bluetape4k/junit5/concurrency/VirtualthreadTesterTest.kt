package io.bluetape4k.junit5.concurrency

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class VirtualthreadTesterTest {

    companion object: KLogging()

    @Test
    fun `run task with virtual thread`() {
        val taskCounter = atomic(0)

        // Virtual thread 1000개를 생성하고 각각 1초씩 sleep
        VirtualthreadTester()
            .numThreads(1000)
            .roundsPerThread(1)
            .add {
                Thread.sleep(1000)
                log.debug { "Run task[${taskCounter.incrementAndGet()}]" }
            }
            .run()

        taskCounter.value shouldBeEqualTo 1000
    }
}
