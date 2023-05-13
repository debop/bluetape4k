package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.collections.eclipse.primitives.intArrayListOf
import io.bluetape4k.coroutines.tests.withSingleThread
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit


/**
 * NOTE: 시간에 따라 Buffering 을 수행하므로, [runTest] 를 사용하지 않고, [runSuspendTest] 를 사용해야 합니다.
 */
class ReplaySubjectSizeAndTimeBoundTest {

    companion object: KLogging()

    @Test
    fun `basic online`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(10, 1L, TimeUnit.MINUTES)
            val result = intArrayListOf()

            val job = launch {
                replay.collect {
                    delay(10)
                    result.add(it)
                }
            }
            replay.awaitCollector()

            repeat(5) {
                replay.emit(it)
            }
            replay.complete()
            job.join()

            result shouldBeEqualTo intArrayListOf(0, 1, 2, 3, 4)
        }
    }

    @Test
    fun `basic offline`() = runSuspendTest {
        val replay = ReplaySubject<Int>(10, 1L, TimeUnit.MINUTES)

        repeat(5) {
            replay.emit(it)
        }
        replay.complete()


        val result = intArrayListOf()
        replay.collect {
            delay(10)
            result.add(it)
        }

        result shouldBeEqualTo intArrayListOf(0, 1, 2, 3, 4)
    }

    @Test
    fun `timed online`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(1L, TimeUnit.MINUTES)
            val result = intArrayListOf()

            val job = launch {
                replay.collect {
                    delay(10)
                    result.add(it)
                }
            }
            replay.awaitCollector()

            repeat(5) {
                replay.emit(it)
            }
            replay.complete()

            job.join()

            result shouldBeEqualTo intArrayListOf(0, 1, 2, 3, 4)
        }
    }

    @Test
    fun `timed offline`() = runSuspendTest {
        val replay = ReplaySubject<Int>(1L, TimeUnit.MINUTES)

        repeat(5) {
            replay.emit(it)
        }
        replay.complete()


        val result = intArrayListOf()
        replay.collect {
            delay(10)
            result.add(it)
        }

        result shouldBeEqualTo intArrayListOf(0, 1, 2, 3, 4)
    }

    @Test
    fun `error online`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(10, 1L, TimeUnit.MINUTES)
            val result = intArrayListOf()
            val exc = atomic<Throwable?>(null)

            val job = launch {
                try {
                    replay.collect {
                        delay(10)
                        result.add(it)
                    }
                } catch (ex: Throwable) {
                    exc.value = ex
                }
            }
            replay.awaitCollector()

            repeat(5) {
                replay.emit(it)
            }
            replay.emitError(RuntimeException())

            job.join()

            result shouldBeEqualTo intArrayListOf(0, 1, 2, 3, 4)
            exc.value shouldBeInstanceOf RuntimeException::class
        }
    }

    @Test
    fun `error offline`() = runSuspendTest {
        val replay = ReplaySubject<Int>(10, 1L, TimeUnit.MINUTES)
        val exc = atomic<Throwable?>(null)

        repeat(5) {
            replay.emit(it)
        }
        replay.emitError(RuntimeException())


        val result = mutableListOf<Int>()
        try {
            replay.collect {
                delay(10)
                result.add(it)
            }
        } catch (ex: Throwable) {
            exc.value = ex
        }

        result shouldBeEqualTo listOf(0, 1, 2, 3, 4)
        exc.value shouldBeInstanceOf RuntimeException::class
    }

    @Test
    fun `take online`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(10, 1L, TimeUnit.MINUTES)
            val result = intArrayListOf()

            val job = launch {
                replay.take(3).collect {
                    delay(10)
                    result.add(it)
                }
            }
            replay.awaitCollector()

            repeat(5) {
                replay.emit(it)
            }
            replay.complete()
            job.join()

            result shouldBeEqualTo intArrayListOf(0, 1, 2)
        }
    }

    @Test
    fun `take offline`() = runSuspendTest {
        val replay = ReplaySubject<Int>(10, 1L, TimeUnit.MINUTES)

        repeat(5) {
            replay.emit(it)
        }
        replay.complete()


        val result = intArrayListOf()
        replay.take(3).collect {
            delay(10)
            result.add(it)
        }

        result shouldBeEqualTo intArrayListOf(0, 1, 2)
    }

    @Test
    fun `bounded online`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(2, 1L, TimeUnit.MINUTES)
            val result = intArrayListOf()

            val job = launch {
                replay.collect {
                    delay(10)
                    result.add(it)
                }
            }
            replay.awaitCollector()

            repeat(5) {
                replay.emit(it)
            }
            replay.complete()

            job.join()

            result shouldBeEqualTo intArrayListOf(0, 1, 2, 3, 4)

            result.clear()
            replay.collect {
                result.add(it)
            }
            result shouldBeEqualTo intArrayListOf(3, 4)
        }
    }

    @Test
    fun `bounded offline`() = runSuspendTest {
        val replay = ReplaySubject<Int>(2, 1L, TimeUnit.MINUTES)

        repeat(5) {
            replay.emit(it)
        }
        replay.complete()


        val result = intArrayListOf()
        replay.collect {
            delay(10)
            result.add(it)
        }

        result shouldBeEqualTo intArrayListOf(3, 4)
    }

    // timeout 이 지난 후에는 남기지 않는다.
    @Test
    fun `timed offline 1`() = runSuspendTest {
        val replay = ReplaySubject<Int>(10, 100, TimeUnit.MILLISECONDS)

        repeat(5) {
            replay.emit(it)
        }
        replay.complete()

        delay(300)

        val result = intArrayListOf()
        replay.collect {
            result.add(it)
        }

        result.isEmpty.shouldBeTrue()
    }

    // timeout 이 지난 후에는 남기지 않는다.
    @Test
    fun `timed offline 2`() = runSuspendTest {
        val replay = ReplaySubject<Int>(10, 100, TimeUnit.MILLISECONDS)

        repeat(3) {
            replay.emit(it)
        }
        delay(300)

        replay.emit(3)
        replay.emit(4)
        replay.complete()

        val result = intArrayListOf()
        replay.collect {
            result.add(it)
        }

        result shouldBeEqualTo intArrayListOf(3, 4)
    }

    @Test
    fun `multiple online`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(10, 1L, TimeUnit.MINUTES)

            val result1 = intArrayListOf()
            val job1 = launch {
                replay.collect {
                    delay(50)
                    log.trace { "collect in job1: $it" }
                    result1.add(it)
                }
            }

            val result2 = intArrayListOf()
            val job2 = launch {
                replay.collect {
                    delay(100)
                    log.trace { "collect in job2: $it" }
                    result2.add(it)
                }
            }

            replay.awaitCollector()

            repeat(5) {
                replay.emit(it)
            }
            replay.complete()

            job1.join()
            job2.join()

            result1 shouldBeEqualTo intArrayListOf(0, 1, 2, 3, 4)
            result2 shouldBeEqualTo intArrayListOf(0, 1, 2, 3, 4)
        }
    }

    @Test
    fun `multiple with take online`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(10, 1L, TimeUnit.MINUTES)

            val result1 = intArrayListOf()
            val job1 = launch {
                replay.collect {
                    delay(50)
                    log.trace { "collect in job1: $it" }
                    result1.add(it)
                }
            }

            val result2 = intArrayListOf()
            val job2 = launch {
                replay.take(3).collect {
                    delay(50)
                    log.trace { "collect in job2: $it" }
                    result2.add(it)
                }
            }

            replay.awaitCollector()

            repeat(5) {
                replay.emit(it)
            }
            replay.complete()

            job1.join()
            job2.join()

            result1 shouldBeEqualTo intArrayListOf(0, 1, 2, 3, 4)
            result2 shouldBeEqualTo intArrayListOf(0, 1, 2)
        }
    }

    @Test
    fun `cancelled consumer`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(20, 1L, TimeUnit.MINUTES)

            val expected = 3
            val n = 10
            val counter1 = atomic(0)

            val job1 = launch {
                replay.collect {
                    log.trace { "collect in job1: $it" }
                    if (counter1.incrementAndGet() == expected) {
                        this.cancel()
                    }
                }
            }

            replay.awaitCollector()

            repeat(n) {
                replay.emit(it)
            }

            repeat(1000) {
                if (job1.isCancelled && replay.collectorCount == 0) {
                    return@repeat
                }
                delay(10)
            }

            job1.isCancelled.shouldBeTrue()
            counter1.value shouldBeEqualTo expected
            replay.collectorCount shouldBeEqualTo 0
        }
    }

    @Test
    fun `cancelled one collector second completes`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(20, 1L, TimeUnit.MINUTES)

            val expected = 3
            val n = 10

            val counter1 = atomic(0)
            val counter2 = atomic(0)

            val job1 = launch {
                replay.collect {
                    log.trace { "collect in job1: $it" }
                    if (counter1.incrementAndGet() == expected) {
                        this.cancel()
                    }
                }
            }
            val job2 = launch {
                replay.collect { counter2.incrementAndGet() }
            }

            replay.awaitCollector()

            repeat(n) {
                replay.emit(it)
            }

            replay.complete()
            job2.join()

            job1.isCancelled.shouldBeTrue()
            job2.isCompleted.shouldBeTrue()

            counter1.value shouldBeEqualTo expected
            counter2.value shouldBeEqualTo n
            replay.collectorCount shouldBeEqualTo 0
        }
    }
}
