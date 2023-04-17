package io.bluetape4k.examples.coroutines.guide

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

class CoroutineContextExamples {

    companion object: KLogging()

    /**
     * CoroutineScope를 delegate를 이용하면 손쉽게 만들 수 있다
     */
    class Activity: CoroutineScope by CoroutineScope(CoroutineName("activity") + Dispatchers.Default) {
        companion object: KLogging()

        fun destroy() {
            cancel()
        }

        fun doSomething() {
            // 10개의 job을 생성합니다.
            repeat(10) {
                log.debug { "Launch job $it." }
                launch {
                    delay((it + 1) * 100L)
                    log.debug { "Job $it is done." }
                }.invokeOnCompletion(onCancelling = true, invokeImmediately = true) { error ->
                    if (error is CancellationException) {
                        log.debug { "Job is cancelled." }
                    }
                }
            }
        }
    }

    @Nested
    inner class Context10 {

        @Test
        fun `CoroutineScope 상속하기`() = runTest {
            val activity = Activity()

            log.debug { "Launched coroutines ..." }
            activity.doSomething()
            delay(10)

            log.debug { "Destroying activity." }
            activity.destroy()      // cancel all child coroutines
        }
    }

    @Nested
    inner class Context8 {
        @Test
        fun `run two coroutines with name`() = runTest(CoroutineName("main")) {
            log.debug { "Started main coroutine" }

            val v1 = async(CoroutineName("v1coroutine")) {
                log.debug { "Starting v1" }
                delay(Random.nextLong(10))
                log.debug { "Computing v1" }
                252
            }

            val v2 = async(CoroutineName("v2coroutine")) {
                log.debug { "Starting v2" }
                delay(Random.nextLong(20))
                log.debug { "Computing v2" }
                6
            }

            val result = v1.await() / v2.await()
            log.debug { "The answer for v1 / v2 = $result" }
        }
    }

    // @Disabled("발표용 코드입니다")
    @Nested
    inner class Basic {
        @Test
        fun `run many coroutines`() = runTest {
            val jobs = List(100_000) {
                launch(Dispatchers.Default) {
                    delay(1000)
                    print(".")
                }
            }
            jobs.joinAll()
        }

        @Test
        fun `run many coroutines with coroutineScope`() = runTest {
            coroutineScope {
                val jobs = List(100_000) {
                    launch(Dispatchers.Default) {
                        delay(1000)
                        print(".")
                    }
                }
                jobs.joinAll()
            }
        }
    }
}
