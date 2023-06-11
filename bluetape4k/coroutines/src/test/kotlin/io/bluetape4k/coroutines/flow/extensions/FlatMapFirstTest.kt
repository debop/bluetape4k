package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class FlatMapFirstTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `flatMapFirst for simple flow`() = runTest {
        flowOf("one", "two")
            .flatMapFirst { v ->
                log.trace { "source item=$v" }
                flow {
                    delay(10L)
                    emit(v)
                }
            }
            .onEach { log.trace { "item=$it" } }
            .assertResult("one")
    }

    @Test
    fun `flatMapFirst for range`() = runTest {
        flowOfRange(1, 10)
            .onEach { delay(100) }
            .flatMapFirst {
                log.trace { "source item=$it" }
                flowOfRange(it * 100, 5)
                    .map { item2 ->
                        delay(20)
                        item2
                    }
            }
            .onEach { log.trace { "item=$it" } }
            .assertResult(
                100, 101, 102, 103, 104,
                300, 301, 302, 303, 304,
                500, 501, 502, 503, 504,
                700, 701, 702, 703, 704,
                900, 901, 902, 903, 904
            )
    }

    @Test
    fun `flat map first with take`() = runTest {

        val item = atomic(0)

        flowOfRange(1, 10)
            .map {
                delay(100)
                it
            }
            .flatMapFirst {
                log.trace { "source item=$it" }
                item.value = it
                flowOfRange(it * 100, 5)
                    .map { item2 ->
                        delay(30)
                        item2
                    }
            }
            .onEach { log.trace { "item=$it" } }
            .take(7)
            .assertResult(
                100, 101, 102, 103, 104,
                300, 301,
            )

        item.value shouldBeEqualTo 3
    }
}
