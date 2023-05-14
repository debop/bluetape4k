package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.collections.eclipse.primitives.intArrayListOf
import io.bluetape4k.coroutines.tests.withSingleThread
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.cancellation.CancellationException

class BehaviorSubjectTest {

    companion object: KLogging()

    @Test
    fun `Behavior subject examples`() = runTest {
        val subject = BehaviorSubject<Int>()

        coroutineScope {
            // HINT: collector 가 등록되지 않았어도, emit 한 요소를 저장해둔다
            // collect 되는 값: 3, 1, 2, 3 (초기 1,2,3 중 마지막 요소만 캐시된다)
            subject.emit(1)
            subject.emit(2)
            subject.emit(3)

            // OR
            // val behaviorSubject = BehaviorSubject<Int>(1)

            val job1 = launch(Dispatchers.IO) {
                subject.collect {
                    log.trace { "Subject1 collect: $it" }
                }
                log.debug { "Done subject1." }
            }
            val job2 = launch(Dispatchers.IO) {
                subject.collect {
                    log.trace { "Subject2 collect: $it" }
                }
                log.debug { "Done subject2." }
            }

            subject.awaitCollectors(2)

            subject.emit(1)
            subject.emit(2)
            subject.emit(3)

            subject.complete()        // 3, 1, 2, 3
            job1.join()
            job2.join()
        }

    }

    @Test
    fun `일반적인 Subject 작동 예`() = runTest {
        withSingleThread { executor ->
            val subject = BehaviorSubject<Int>()
            val result = intArrayListOf()

            val job = launch(executor) {
                subject.collect {
                    log.trace { "collect: $it" }
                    delay(100)
                    result.add(it)
                }
            }
            subject.awaitCollector()

            repeat(5) {
                subject.emit(it + 1)
            }
            subject.complete()
            job.join()

            result shouldBeEqualTo intArrayListOf(1, 2, 3, 4, 5)
        }
    }

    @Test
    fun `많은 수의 Item을 제공`() = runTest {
        val subject = BehaviorSubject<Int>()
        val counter = atomic(0)
        val n = 100_000

        val job = launch {
            subject.collect {
                counter.incrementAndGet()
            }
        }

        subject.awaitCollector()

        repeat(n) {
            subject.emit(it)
        }

        subject.complete()
        job.join()

        counter.value shouldBeEqualTo n
    }

    @Test
    fun `예외를 emit 하면 예외가 전달된다`() = runTest {
        val subject = BehaviorSubject<Int>()
        val counter = atomic(0)
        val error = atomic<Throwable?>(null)

        withSingleThread { executor ->
            val job = launch(executor) {
                try {
                    subject.collect {
                        counter.incrementAndGet()
                    }
                } catch (e: Throwable) {
                    error.value = e
                }
            }

            subject.awaitCollector()

            subject.emit(1)
            subject.emit(2)
            subject.emitError(RuntimeException())
            subject.emit(3)
            subject.complete()

            job.join()
        }
        counter.value shouldBeEqualTo 2
        error.value shouldBeInstanceOf RuntimeException::class
    }

    @Test
    fun `복수의 Consumer 가 있는 경우`() = runTest {
        val subject = BehaviorSubject<Int>()

        val n = 1000
        val counter1 = atomic(0)
        val counter2 = atomic(0)

        val job1 = launch {
            subject.collect {
                counter1.incrementAndGet()
            }
        }
        val job2 = launch {
            subject.collect {
                counter2.incrementAndGet()
            }
        }

        subject.awaitCollectors(2)

        repeat(n) {
            subject.emit(it + 1)
        }

        subject.complete()
        job1.join()
        job2.join()

        counter1.value shouldBeEqualTo n
        counter2.value shouldBeEqualTo n
    }

    @Test
    fun `복수의 Consumer 가 다른 take를 적용할 때`() = runTest {
        val subject = BehaviorSubject<Int>()

        val n = 1000
        val counter1 = atomic(0)
        val counter2 = atomic(0)

        val job1 = launch {
            subject
                .take(n / 2)
                .collect {
                    counter1.incrementAndGet()
                }
        }
        val job2 = launch {
            subject
                .take(n / 5)
                .collect {
                    counter2.incrementAndGet()
                }
        }

        subject.awaitCollectors(2)

        repeat(n) {
            subject.emit(it + 1)
        }

        subject.complete()
        job1.join()
        job2.join()

        counter1.value shouldBeEqualTo n / 2
        counter2.value shouldBeEqualTo n / 5
    }

    @Test
    fun `초기 값이 주어진 경우`() = runTest {
        val subject = BehaviorSubject(0)
        val result = CopyOnWriteArrayList<Int>()

        withSingleThread { executor ->
            val job = launch(executor) {
                subject.collect {
                    delay(100)
                    result.add(it)
                }
            }

            subject.awaitCollector()

            repeat(5) {
                subject.emit(it + 1)
            }

            subject.complete()
            job.join()
        }
        result shouldBeEqualTo listOf(0, 1, 2, 3, 4, 5)
    }

    @Test
    fun `collector 가 cancel 을 하게되면 `() = runTest {
        val subject = BehaviorSubject<Int>()

        val n = 10
        val counter1 = atomic(0)
        val counter2 = atomic(0)
        val expected = 3

        withSingleThread { executor ->
            val job1 = launch(executor) {
                subject
                    .onEach { log.trace { "job2: $it" } }
                    .collect {
                        if (counter1.incrementAndGet() == expected) {
                            throw CancellationException()
                        }
                    }
            }
            val job2 = launch(executor) {
                subject
                    .onEach { log.trace { "job2: $it" } }
                    .collect {
                        counter2.incrementAndGet()
                    }
            }

            subject.awaitCollectors(2)

            repeat(n) {
                subject.emit(it + 1)
            }

            subject.complete()
            job1.join()
            job2.join()
        }

        counter1.value shouldBeEqualTo expected
        counter2.value shouldBeEqualTo n
    }
}
