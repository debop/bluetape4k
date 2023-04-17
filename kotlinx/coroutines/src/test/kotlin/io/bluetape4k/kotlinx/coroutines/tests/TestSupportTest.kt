package io.bluetape4k.kotlinx.coroutines.tests

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import kotlin.random.Random

class TestSupportTest {

    companion object: KLogging()

    @Test
    fun `run coroutine with single thread`() = runTest {
        log.debug { "Start" }

        withSingleThread { executor ->
            var job1ThreadName = ""
            var job2ThreadName = ""

            // job1, job2 는 같은 스레드에서 실행되지만, CoroutineContext 는 다르다.
            //
            val job1 = launch(executor) {
                delay(400)
                job1ThreadName = Thread.currentThread().name.substringBefore("@coroutine")
                log.debug { "First coroutine" }
            }
            val job2 = launch(executor) {
                delay(200)
                job2ThreadName = Thread.currentThread().name.substringBefore("@coroutine")
                log.debug { "Second coroutine" }
            }
            advanceUntilIdle()

            job1.join()
            job2.join()

            job2ThreadName shouldBeEqualTo job1ThreadName
        }
        log.debug { "Done" }
    }

    @Test
    @Timeout(5)
    fun `run many coroutines with single thread`() = runTest {
        // Single Thread 에서 2000개의 코루틴을 실행합니다.
        withSingleThread { executor ->
            val jobs1 = List(1000) {
                launch(executor) {
                    delay(Random.nextLong(1, 5))
                    log.debug { "Job1[$it]" }
                }
            }
            val jobs2 = List(1000) {
                launch(executor) {
                    delay(Random.nextLong(1, 5))
                    log.debug { "Job2[$it]" }
                }
            }
            advanceUntilIdle()

            jobs1.joinAll()
            jobs2.joinAll()
        }
        log.debug { "Done" }
    }
}
