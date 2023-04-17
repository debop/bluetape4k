package io.bluetape4k.examples.coroutines.guide

import io.bluetape4k.logging.KLogging
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ForkJoinPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.RepeatedTest

class CoroutineExamples {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 2
        private const val ITEM_SIZE = 100_000
        private const val DELAY_TIME = 10L
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `run tasks in coroutines`() = runTest {
        coroutineScope {
            List(ITEM_SIZE) {
                launch(Dispatchers.IO) {
                    delay(DELAY_TIME)
                }
            }
        }
    }

    @Disabled("Blocking 방식이라 실행이 겁나 오래 걸립니다")
    @RepeatedTest(REPEAT_SIZE)
    fun `run tasks in thread pool`() {
        val latch = CountDownLatch(ITEM_SIZE)
        val executor = ForkJoinPool.commonPool()
        List(ITEM_SIZE) {
            executor.execute {
                Thread.sleep(DELAY_TIME)
                latch.countDown()
            }
        }
        latch.await()
    }
}
