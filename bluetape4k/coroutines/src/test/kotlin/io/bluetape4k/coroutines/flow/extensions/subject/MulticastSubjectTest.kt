package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.coroutines.tests.withSingleThread
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.util.concurrent.CopyOnWriteArrayList


/**
 * [MulticastSubject]
 *
 * A subject implementation that awaits a certain number of collectors
 * to start consuming, then allows the producer side to deliver items
 * to them.
 */
class MulticastSubjectTest {

    companion object: KLogging()

    @Test
    fun `multicast subject`() = runTest {
        val subject = MulticastSubject<Int>(1)
        val result = CopyOnWriteArrayList<Int>()

        withSingleThread { dispatcher ->
            val job = launch(dispatcher) {
                subject.collect {
                    log.trace { "collect: $it" }
                    delay(10)
                    result.add(it)
                }
            }
            // collector가 등록되어 실행될 때까지 대기합니다.
            subject.awaitCollector()

            repeat(10) {
                log.trace { "emit: ${it + 1}" }
                subject.emit(it + 1)
            }
            subject.complete()
            job.join()
        }
        result shouldBeEqualTo List(10) { it + 1 }
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
            }
            subject.awaitCollector()

            repeat(n) {
                subject.emit(it + 1)
            }
            subject.complete()
            job.join()
        }
        counter.value shouldBeEqualTo n
    }

    @Test
    fun `lot of items with multiple consumers`() = runTest {
        val subject = MulticastSubject<Int>(1)
        val n = 1_000
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
                subject.emit(it + 1)
            }

            subject.complete()
            job1.join()
            job2.join()
        }
        counter1.value shouldBeEqualTo n
        counter2.value shouldBeEqualTo n
    }
}
