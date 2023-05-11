package io.bluetape4k.examples.coroutines.builders

import io.bluetape4k.collections.eclipse.fastList
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
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
            log.debug { "Start" }

            launch {
                delay(1000)
                log.debug { "World 1" }
            }
            launch {
                delay(1000)
                log.debug { "World 2" }
            }
            log.debug { "Hello, " }
            delay(1000)
            advanceUntilIdle()
            log.debug { "Finish." }
        }
    }

    @Nested
    inner class AsyncExample {

        @Test
        fun `async builder example`() = runTest {
            val resultDeferred = async {
                delay(1000L)
                log.debug { "Return result." }
                42
            }
            log.debug { "Build async and await ..." }
            val result = resultDeferred.await()
            log.debug { "result=$result" }
            log.debug { "result=${resultDeferred.await()}" }
            log.debug { "Finish" }
        }

        @Test
        fun `await returns`() = runTest {
            val results = fastList(10) {
                async {
                    delay(Random.nextLong(500, 1000))
                    log.debug { "Return $it" }
                    "Result $it"
                }
            }
            advanceUntilIdle()
            val res = results.awaitAll()
            log.debug { "Result=${res.joinToString()}" }
        }
    }
}
