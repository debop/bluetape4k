package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class FlatMapDropTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `flat map drop`() = runTest {
        flowRangeOf(1, 10)
            .onEach { delay(100) }.log("source")
            .flatMapDrop {
                flowRangeOf(it * 100, 5).onEach { delay(20) }.log("inner")
            }
            .assertResult(
                100, 101, 102, 103, 104,
                300, 301, 302, 303, 304,
                500, 501, 502, 503, 504,
                700, 701, 702, 703, 704,
                900, 901, 902, 903, 904
            )
    }

    @Test
    fun `flat map drop with take`() = runTest {
        val item = atomic(0)

        flowRangeOf(1, 10)
            .onEach { delay(100) }.log("source")
            .flatMapDrop {
                item.value = it
                flowRangeOf(it * 100, 5)
                    .onEach { delay(30) }.log("inner")
            }
            .take(7).log("take 7")
            .assertResult(
                100, 101, 102, 103, 104,
                300, 301,
            )

        item.value shouldBeEqualTo 3
    }
}
