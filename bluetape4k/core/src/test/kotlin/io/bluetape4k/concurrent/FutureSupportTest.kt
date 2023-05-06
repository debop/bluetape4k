package io.bluetape4k.concurrent

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.Test
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
}
