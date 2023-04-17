package io.bluetape4k.examples.coroutines.builders

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.random.Random

class CoroutineBuilderExamples {

    companion object: KLogging()

    @Nested
    inner class LaunchExample {

        @Test
        fun `launch coroutines`() = runTest {
            log.info { "Start" }

            launch {
                delay(1000)
                log.info { "World 1" }
            }
            launch {
                delay(1000)
                log.info { "World 2" }
            }
            log.info { "Hello, " }
            delay(1000)
            advanceUntilIdle()
            log.info { "Finish." }
        }
    }

    @Nested
    inner class AsyncExample {

        @Test
        fun `async builder example`() = runTest {
            val resultDeferred = async {
                delay(1000L)
                log.info { "Return result." }
                42
            }
            log.info { "Build async and await ..." }
            val result = resultDeferred.await()
            log.info { "result=$result" }
            log.info { "result=${resultDeferred.await()}" }
            log.info { "Finish" }
        }

        @Test
        fun `await returns`() = runTest {
            val results = List(10) {
                async {
                    delay(Random.nextLong(500, 1000))
                    log.info { "Return $it" }
                    "Result $it"
                }
            }
            advanceUntilIdle()
            val res = results.awaitAll()
            log.info { "Result=${res.joinToString()}" }
        }
    }
}
