package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.collections.eclipse.primitives.intArrayListOf
import io.bluetape4k.collections.eclipse.ranges.toIntArrayList
import io.bluetape4k.coroutines.tests.withSingleThread
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.assertFailsWith

class PublishSubjectTest {

    companion object: KLogging()

    @Test
    fun `multicast values to one or more flow collectors`() = runTest {
        val subject = PublishSubject<Int>()

        val result1 = intArrayListOf()
        val result2 = intArrayListOf()

        coroutineScope {

            launch(Dispatchers.IO) {
                subject.collect {
                    log.trace { "Subject1 collect: $it" }
                    result1.add(it)
                }
                log.info { "Done Job1" }
            }
            launch(Dispatchers.IO) {
                subject.collect {
                    log.trace { "Subject2 collect: $it" }
                    result2.add(it)
                }
                log.info { "Done Job2" }
            } // subject1 만 시작해도 다음으로 넘어간다 (subject2가 시작 안했을 수 있다)

            yield()

            // collector가 실행될 때까지 대기합니다.
            subject.awaitCollectors(2)

            repeat(10) {
                log.trace { "emit: ${it + 1}" }
                subject.emit(it + 1)
            }
            subject.complete()

            // HINT: coroutineScope {} 를 쓰면 children 이 끝날 때까지 대기하므로 job join은 필요없다
            //        job1.join()
            //        job2.join()
        }
        val expected = (1..10).toIntArrayList()
        result1 shouldBeEqualTo expected
        result2 shouldBeEqualTo expected
    }

    @Test
    fun `basic create`() = runTest {
        val result1 = intArrayListOf()
        val result2 = intArrayListOf()

        withSingleThread { dispatcher ->
            val subject = PublishSubject<Int>()

            val job1 = launch(dispatcher) {
                subject.collect {
                    delay(100)
                    log.trace { "collect1: $it" }
                    result1.add(it)
                }
            }
            val job2 = launch(dispatcher) {
                subject.collect {
                    delay(100)
                    log.trace { "collect2: $it" }
                    result2.add(it)
                }
            }

            subject.awaitCollectors(2)

            subject.emit(1)
            subject.emit(2)
            subject.emit(3)
            subject.emit(4)
            subject.emit(5)
            subject.complete()

            job1.join()
            job2.join()
        }
        result1 shouldBeEqualTo intArrayListOf(1, 2, 3, 4, 5)
        result2 shouldBeEqualTo intArrayListOf(1, 2, 3, 4, 5)
    }

    @Test
    fun `emit and collect many items`() = runTest {
        withSingleThread { dispatcher ->
            val subject = PublishSubject<Int>()
            val n = 10_000
            val counter = atomic(0)
            val count by counter

            val job = launch(dispatcher) {
                subject.collect {
                    counter.incrementAndGet()
                }
            }

            subject.awaitCollectors(1)

            repeat(n) { i ->
                subject.emit(i)
            }
            subject.complete()
            job.join()

            count shouldBeEqualTo n
        }
    }

    @Test
    fun `예외를 emit 한 경우 try-catch로 잡을 수 있다`() = runTest {
        withSingleThread { dispatcher ->
            val subject = PublishSubject<Int>()
            val counter = atomic(0)
            val count by counter
            val error = atomic<Throwable?>(null)

            val job = launch(dispatcher) {
                try {
                    subject.collect {
                        counter.incrementAndGet()
                    }
                } catch (e: Throwable) {
                    error.value = e
                }
            }

            while (!subject.hasCollectors) {
                delay(1)
            }

            subject.emitError(RuntimeException("Boom!"))
            subject.complete()

            job.join()

            count shouldBeEqualTo 0
            error.value shouldBeInstanceOf RuntimeException::class
        }
    }

    @Test
    fun `multiple consumer`() = runTest {
        val subject = PublishSubject<Int>()
        val n = 10_000
        val counter1 = atomic(0)
        val counter2 = atomic(0)

        withSingleThread { dispatcher ->
            val job1 = launch(dispatcher) {
                subject.collect {
                    counter1.incrementAndGet()
                }
            }
            val job2 = launch(dispatcher) {
                subject.collect {
                    counter2.incrementAndGet()
                }
            }

            subject.awaitCollectors(2)

            repeat(n) {
                subject.emit(it)
            }
            subject.complete()

            job1.join()
            job2.join()
        }
        counter1.value shouldBeEqualTo n
        counter2.value shouldBeEqualTo n
    }

    @Test
    fun `multiple consumer with different delay`() = runTest {
        coroutineScope {
            val subject = PublishSubject<Int>()
            val n = 100

            val counter1 = atomic(0)
            val counter2 = atomic(0)

            val job1 = launch {
                subject.collect {
                    delay(1)
                    counter1.incrementAndGet()
                }
            }
            val job2 = launch {
                subject.collect {
                    delay(2)
                    counter2.incrementAndGet()
                }
            }

            subject.awaitCollectors(2)

            repeat(n) {
                subject.emit(it)
            }
            subject.complete()

            // coroutineScope 내에서는 굳이 join 으로 대기할 필요 없다
            job1.join()
            job2.join()

            counter1.value shouldBeEqualTo n
            counter2.value shouldBeEqualTo n
        }
    }

    @Test
    fun `multiple consumer with take operation`() = runTest {
        coroutineScope {
            val subject = PublishSubject<Int>()
            val n = 100

            val counter1 = atomic(0)
            val counter2 = atomic(0)

            launch {
                subject.collect {
                    delay(1)
                    counter1.incrementAndGet()
                }
            }
            launch {
                subject.take(n / 2).collect {
                    counter2.incrementAndGet()
                }
            }

            subject.awaitCollectors(2)

            repeat(n) {
                subject.emit(it)
            }
            subject.complete()

            counter1.value shouldBeEqualTo n
            counter2.value shouldBeEqualTo n / 2
        }
    }

    @Test
    fun `이미 completed 된 subject는 collect 할 수 없다`() = runTest {
        val subject = PublishSubject<Int>()
        subject.complete()

        val counter = atomic(0)

        // subject가 이미 completed 되었으므로, collect 를 수행하지 않습니다.
        subject.collect {
            counter.incrementAndGet()
        } // completed 이후에는 collect 가 동작하지 않는다
        counter.value shouldBeEqualTo 0
    }

    @Test
    fun `이미 예외가 emit 된 경우 collect 할 수 없다`() = runTest {
        val subject = PublishSubject<Int>()
        subject.emitError(RuntimeException("Boom!"))

        val counter = atomic(0)

        // subject 에서 예외를 emit 했으므로, collect 시에 emit된 예외가 발생합니다.
        assertFailsWith<RuntimeException> {
            subject.collect {
                counter.incrementAndGet()
            }
        } // completed 이후에는 collect 가 동작하지 않는다
        counter.value shouldBeEqualTo 0
    }

    @Test
    fun `collect 시 예외가 발생하면 collector 를 제거한다`() = runTest {
        coroutineScope {
            val subject = PublishSubject<Int>()
            val n = 10
            val expected = 3

            val counter1 = atomic(0)

            val job = launch(Dispatchers.IO) {
                subject.collect {
                    if (counter1.incrementAndGet() == expected) {
                        throw CancellationException()
                    }
                }
            }

            while (!subject.hasCollectors) {
                delay(1)
            }

            repeat(n) {
                subject.emit(it)
            }
            subject.complete()

            for (i in 1..1000) {
                if (job.isCancelled && subject.collectorCount == 0) {
                    break
                }
                delay(1)
            }

            job.isCancelled.shouldBeTrue()
            subject.collectorCount shouldBeEqualTo 0

            counter1.value shouldBeEqualTo expected
        }
    }
}
