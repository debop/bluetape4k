package io.bluetape4k.coroutines.support

import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.util.concurrent.FutureTask
import kotlin.random.Random

class FutureExtensionsTest {

    companion object: KLogging() {
        private const val ITEM_COUNT = 128
        private const val DELAY_TIME = 100L
    }

    @Test
    fun `Massive Futre as CompletaboeFuture in Multiple Thread`() = runSuspendTest {
        val counter = atomic(0)

        MultithreadingTester()
            .numThreads(16)
            .roundsPerThread(ITEM_COUNT / 4)
            .add {
                runBlocking {
                    val task: FutureTask<Int> = FutureTask {
                        Thread.sleep(Random.nextLong(10))
                        log.trace { "counter=${counter.value}" }
                        counter.incrementAndGet()
                    }
                    task.run()
                    val result = task.awaitSuspending()
                    log.trace { "result=$result" }
                }
            }
            .run()

        counter.value shouldBeEqualTo 4 * ITEM_COUNT
    }

    @Test
    fun `Massive Futre as CompletaboeFuture in Multiple Coroutines`() = runSuspendTest {
        val counter = atomic(0)

        MultiJobTester()
            .numJobs(4)
            .roundsPerJob(ITEM_COUNT)
            .add {
                val task: FutureTask<Int> = FutureTask {
                    Thread.sleep(Random.nextLong(10))
                    log.trace { "counter=${counter.value}" }
                    counter.incrementAndGet()
                }
                task.run()
                val result = task.awaitSuspending()
                log.trace { "result=$result" }
            }
            .run()

        counter.value shouldBeEqualTo 4 * ITEM_COUNT
    }
}
