package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.coroutines.tests.withSingleThread
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class BufferedResumableCollectorTest {

    companion object: KLogging()

    @Test
    fun `basic long operations with 32 capacity`() = runTest {
        val bc = BufferedResumableCollector<Int>(32)
        val n = 10_000
        val counter = atomic(0)
        val count by counter

        withSingleThread { dispatcher ->
            val job = launch(dispatcher) {
                repeat(n) {
                    log.trace { "next: $it" }
                    bc.next(it)
                }
                bc.complete()
            }

            val collector = FlowCollector<Int> {
                log.trace { "drain: $it" }
                counter.incrementAndGet()
            }

            bc.drain(collector)
            job.join()
        }
        count shouldBeEqualTo n
    }

    @Test
    fun `basic long operation with one capacity`() = runTest {
        val bc = BufferedResumableCollector<Int>(1)
        val n = 10_000
        val counter = atomic(0)
        val count by counter

        withSingleThread { dispatcher ->
            val job = launch(dispatcher) {
                repeat(n) {
                    log.trace { "next: $it" }
                    bc.next(it)
                }
                bc.complete()
            }
            val collector = FlowCollector<Int> {
                log.trace { "drain: $it" }
                counter.incrementAndGet()
            }

            bc.drain(collector)
            job.join()
        }
        count shouldBeEqualTo n
    }

    @Test
    fun `basic long operations with 64 capacity`() = runTest {
        val bc = BufferedResumableCollector<Int>(64)
        val n = 100_000
        val counter = atomic(0)
        val count by counter

        withSingleThread { dispatcher ->
            val job = launch(dispatcher) {
                repeat(n) {
                    bc.next(it)
                }
                bc.complete()
            }

            val collector = FlowCollector<Int> { counter.incrementAndGet() }
            bc.drain(collector)

            job.join()
        }
        count shouldBeEqualTo n
    }

    @Test
    fun `basic long operations with 256 capacity`() = runTest {
        val bc = BufferedResumableCollector<Int>(256)
        val n = 100_000
        val counter = atomic(0)
        val count by counter

        withSingleThread { dispatcher ->
            val job = launch(dispatcher) {
                repeat(n) {
                    bc.next(it)
                }
                bc.complete()
            }

            val collector = FlowCollector<Int> { counter.incrementAndGet() }
            bc.drain(collector)
            job.join()
        }
        count shouldBeEqualTo n
    }
}
