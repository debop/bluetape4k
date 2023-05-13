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
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicReference

class ReplaySubjectSizeBoundTest {

    companion object: KLogging()

    @Test
    fun `basic online`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(10)
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
        val replay = ReplaySubject<Int>(10)

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
            val replay = ReplaySubject<Int>(10)

            val result = intArrayListOf()
            val exc = AtomicReference<Throwable>()

            val job = launch {
                try {
                    replay.collect {
                        delay(10)
                        result.add(it)
                    }
                } catch (e: Throwable) {
                    exc.set(e)
                }
            }
            replay.awaitCollector()

            repeat(5) {
                replay.emit(it)
            }
            replay.emitError(RuntimeException())

            job.join()

            result shouldBeEqualTo intArrayListOf(0, 1, 2, 3, 4)
            exc.get() shouldBeInstanceOf RuntimeException::class
        }
    }

    @Test
    fun `error offline`() = runSuspendTest {
        val replay = ReplaySubject<Int>(10)

        val result = intArrayListOf()
        val exc = AtomicReference<Throwable>()

        repeat(5) {
            replay.emit(it)
        }
        replay.emitError(RuntimeException())

        try {
            replay.collect {
                result.add(it)
            }
        } catch (e: Throwable) {
            exc.set(e)
        }

        result shouldBeEqualTo intArrayListOf(0, 1, 2, 3, 4)
        exc.get() shouldBeInstanceOf RuntimeException::class
    }

    @Test
    fun `take online`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(10)
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
        val replay = ReplaySubject<Int>(10)

        repeat(5) {
            replay.emit(it)
        }
        replay.complete()


        val result = intArrayListOf()
        replay.take(3).collect {
            result.add(it)
        }

        result shouldBeEqualTo intArrayListOf(0, 1, 2)
    }

    @Test
    fun `bounded online`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(2)
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
        val replay = ReplaySubject<Int>(2)

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

    @Test
    fun `multiple online`() = runSuspendTest {
        withSingleThread {
            val replay = ReplaySubject<Int>(10)

            val result1 = intArrayListOf()
            val job1 = launch {
                replay.collect {
                    delay(50)
                    result1.add(it)
                }
            }

            val result2 = intArrayListOf()
            val job2 = launch {
                replay.collect {
                    delay(100)
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
            val replay = ReplaySubject<Int>(10)

            val result1 = intArrayListOf()
            val job1 = launch {
                replay.collect {
                    delay(50)
                    result1.add(it)
                }
            }

            val result2 = intArrayListOf()
            val job2 = launch {
                replay.take(3).collect {
                    delay(100)
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
            val replay = ReplaySubject<Int>(20)

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
            val replay = ReplaySubject<Int>(20)

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
