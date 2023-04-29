package io.bluetape4k.examples.coroutines.cancellation

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.logging.info
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import kotlin.coroutines.cancellation.CancellationException

class CancellationExamples {

    companion object: KLogging()

    @Test
    fun `Basic cancellation`() = runTest {
        val counter = atomic(0L)

        log.debug { "Start Job." }

        val job = launch {
            repeat(1000) { i ->
                delay(200)
                counter.incrementAndGet()
                log.debug { "Printing $i" }
            }
        }
        delay(1100)
        job.cancel()
        job.join()
        counter.value shouldBeEqualTo 5L
        log.debug { "Cancelled successfully." }
    }

    @Test
    fun `작업 취소 시는 cancellation 예외를 catch 합니다`() = runTest {
        val job = Job()
        launch(job) {
            try {
                repeat(1000) {
                    delay(200)
                    log.debug { "Printing $it" }
                }
            } catch (e: CancellationException) {
                log.error(e) { "Job이 취소되었습니다" }
                throw e
            }
        }
        delay(1100)
        job.cancelAndJoin()
        log.debug { "Cancelled successfully" }
        advanceUntilIdle()
    }

    @Test
    fun `NonCancellable context 하에서 취소 시에도 정리 작업 수행하기`() = runTest {
        var counter = 0
        var cleanup = false
        val job = Job()
        launch(job) {
            try {
                delay(200)
                // 이 작업은 수행되지 않습니다.
                counter++
                log.debug { "Coroutine finished" }
            } finally {
                log.debug { "Finally" }
                // 취소 시에도 무조건 작업을 수행하도록 합니다.
                withContext(NonCancellable) {
                    delay(1000)
                    cleanup = true
                    log.debug { "Cleanup done" }
                }
            }
        }

        delay(100)
        job.cancelAndJoin()
        log.info { "Done" }

        counter shouldBeEqualTo 0 // 작업이 cancel 되므로 ...
        cleanup.shouldBeTrue()
    }

    @Test
    fun `invokeOnCompletion event listener 로 취소 시 작업 수행`() = runTest {
        val canceled = atomic(false)
        val job = launch { delay(1000) }

        // invoeOnCompletion Handler를 사용하여, Cancel 에 대한 처리를 수행할 수 있습니다.
        job.invokeOnCompletion { cause: Throwable? ->
            if (cause is CancellationException) {
                canceled.value = true
                log.info { "Cancelled" }
            } else {
                log.info { "Finished" }
            }
        }
        //        job.invokeOnCompletion(false, true) {
        //            if (it != null) {
        //                canceled.set(true)
        //                log.info(it) { "Cancelled" }
        //            }
        //        }
        delay(100)
        job.cancelAndJoin()     // Cancelled

        canceled.value.shouldBeTrue()
    }

    @Test
    fun `Job isActive 를 활용하여 suspend point 잡기`() = runTest {
        val counter = AtomicInteger()
        val job = Job()
        launch(job) {
            while (isActive) {
                delay(100)         // delay 나 yield 로 suspend point 를 줘야 `isActive` 를 조회할 수 있다
                counter.incrementAndGet()
                log.debug { "Printing" }
            }
        }
        // runBlocking 으로 테스트 시에는 withContext 내에서 await 를 수행해야 합니다.
        //        withContext(Dispatchers.Default) {
        //            await until { counter.get() == 5 }
        //        }
        delay(550)

        job.cancelAndJoin()
        counter.get() shouldBeGreaterOrEqualTo 5
        log.info { "Cancelled successfully." }
    }
}
