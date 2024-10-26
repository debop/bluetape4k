package io.bluetape4k.concurrent

import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.junit5.concurrency.VirtualthreadTester
import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import kotlin.random.Random

class FutureSupportTest {

    companion object: KLogging() {
        private const val ITEM_COUNT = 100
        private const val DELAY_TIME = 100L
    }

    @Test
    fun `Future as CompletableFuture`() {
        val future1 = FutureTask {
            Thread.sleep(DELAY_TIME)
            "value1"
        }
        future1.run()

        val future2 = FutureTask {
            Thread.sleep(DELAY_TIME)
            "value2"
        }
        future2.run()

        val result1 = future1.asCompletableFuture()
        val result2 = future2.asCompletableFuture()
        result1.join() shouldBeEqualTo "value1"
        result2.join() shouldBeEqualTo "value2"
    }

    @Test
    fun `Massive Future as CompletableFuture`() {
        val futures = List(ITEM_COUNT) {
            FutureTask {
                Thread.sleep(Random.nextLong(10))
                "value$it"
            }.apply { run() }
        }.map { it.asCompletableFuture() }

        val results = futures.sequence()
        await until { results.isDone }

        val values = results.join()
        values.size shouldBeEqualTo ITEM_COUNT
    }

    @Test
    fun `Massive Future as CompletaboeFuture in Multiple Thread`() {
        val counter = atomic(0)
        val executor = Executors.newCachedThreadPool()
        MultithreadingTester()
            .numThreads(16)
            .roundsPerThread(ITEM_COUNT / 4)
            .add {
                val task = executor.submit<Int> {
                    Thread.sleep(Random.nextLong(10))
                    counter.incrementAndGet()
                }
                val future = task.asCompletableFuture()
                await until { future.isDone }
                log.debug { "result=${future.get()}" }
            }
            .run()

        counter.value shouldBeEqualTo 4 * ITEM_COUNT
    }

    @Test
    fun `Massive Future as CompletaboeFuture in Multiple Virtual Thread`() {
        val counter = atomic(0)
        val executor = Executors.newVirtualThreadPerTaskExecutor()
        VirtualthreadTester()
            .numThreads(16)
            .roundsPerThread(ITEM_COUNT / 4)
            .add {
                val task = executor.submit<Int> {
                    Thread.sleep(Random.nextLong(10))
                    counter.incrementAndGet()
                }
                val future = task.asCompletableFuture()
                await until { future.isDone }
                log.debug { "result=${future.get()}" }
            }
            .run()

        counter.value shouldBeEqualTo 4 * ITEM_COUNT
    }

    @Test
    fun `Massive Future as CompletaboeFuture in Multiple Coroutines`() = runTest {
        val counter = atomic(0)

        MultiJobTester()
            .numJobs(16)
            .roundsPerJob(ITEM_COUNT / 4)
            .add {
                val task = async(Dispatchers.IO) {
                    delay(Random.nextLong(10))
                    counter.incrementAndGet()
                }
                val result = task.await()
                log.debug { "result=$result" }
            }
            .run()

        counter.value shouldBeEqualTo 4 * ITEM_COUNT
    }
}
