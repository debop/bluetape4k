package io.bluetape4k.coroutines.tests

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Timeout
import kotlin.random.Random

class TestSupportTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `run coroutine with single thread`() = runTest {
        log.trace { "Start" }

        withSingleThread { executor ->
            var job1ThreadName = ""
            var job2ThreadName = ""

            // job1, job2 는 같은 스레드에서 실행되지만, CoroutineContext 는 다르다.
            //
            val job1 = launch(executor) {
                delay(40)
                job1ThreadName = Thread.currentThread().name.substringBefore("@coroutine")
                log.debug { "First coroutine. thread name=$job1ThreadName" }
            }
            val job2 = launch(executor) {
                delay(20)
                job2ThreadName = Thread.currentThread().name.substringBefore("@coroutine")
                log.debug { "Second coroutine. thread name=$job2ThreadName" }
            }
            advanceUntilIdle()

            job1.join()
            job2.join()

            job2ThreadName shouldBeEqualTo job1ThreadName
        }
        log.trace { "Done" }
    }

    @RepeatedTest(REPEAT_SIZE)
    @Timeout(5)
    fun `run many coroutines with single thread`() = runTest {
        // Single Thread 에서 2000개의 코루틴을 실행합니다.
        withSingleThread { executor ->
            val jobs1 = List(1000) {
                launch(executor) {
                    delay(Random.nextLong(1, 5))
                    log.trace { "Job1[$it]" }
                }
            }
            val jobs2 = List(1000) {
                launch(executor) {
                    delay(Random.nextLong(1, 5))
                    log.trace { "Job2[$it]" }
                }
            }
            advanceUntilIdle()

            jobs1.joinAll()
            jobs2.joinAll()
        }
        log.trace { "Done" }
    }
}
