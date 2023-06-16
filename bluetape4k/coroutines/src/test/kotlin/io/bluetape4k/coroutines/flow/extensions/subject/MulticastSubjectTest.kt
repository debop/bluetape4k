package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.coroutines.flow.extensions.log
import io.bluetape4k.coroutines.support.log
import io.bluetape4k.coroutines.tests.withSingleThread
import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.util.concurrent.CopyOnWriteArrayList

class MulticastSubjectTest {

    companion object: KLogging()

    @Test
    fun `1개의 collector 가 등록될 때까지 producer가 대기합니다`() = runTest {
        val subject = MulticastSubject<Int>(1)
        val result = CopyOnWriteArrayList<Int>()

        withSingleThread { dispatcher ->
            val job = launch(dispatcher) {
                subject
                    .onEach { delay(10) }
                    .log("#1")
                    .collect { result.add(it) }
            }.log("job")

            // collector가 등록되어 실행될 때까지 대기합니다.
            subject.awaitCollector()

            repeat(10) {
                subject.emit(it)
            }
            subject.complete()
            job.join()
        }
        result shouldBeEqualTo List(10) { it }
    }

    @Test
    fun `lot of items`() = runTest {
        val subject = MulticastSubject<Int>(1)
        val n = 1_000
        val counter = atomic(0)

        withSingleThread { dispatcher ->
            val job = launch(dispatcher) {
                subject.collect {
                    counter.incrementAndGet()
                }
            }.log("job1")

            subject.awaitCollector()

            repeat(n) {
                subject.emit(it)
            }
            subject.complete()
            job.join()
        }
        counter.value shouldBeEqualTo n
    }

    @Test
    fun `2개의 collector 가 등록될 때까지 producer는 대기합니다`() = runTest {
        val subject = MulticastSubject<Int>(2)
        val n = 1_000
        val counter1 = atomic(0)
        val counter2 = atomic(0)

        withSingleThread { dispatcher ->
            val job1 = launch(dispatcher) {
                subject.collect {
                    counter1.incrementAndGet()
                }
            }.log("job1")

            val job2 = launch(dispatcher) {
                subject.collect {
                    counter2.incrementAndGet()
                }
            }.log("job2")

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
}
