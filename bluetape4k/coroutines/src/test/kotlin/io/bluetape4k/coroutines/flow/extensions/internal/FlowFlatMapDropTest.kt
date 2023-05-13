package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.flatMapDrop
import io.bluetape4k.coroutines.flow.extensions.flowOfRange
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class FlowFlatMapDropTest {

    companion object: KLogging()

    // 시간 관련이라 runTest 로는 예측할 수 없는 결과가 나옵니다.
    @Test
    fun `flat map drop`() = runTest {
        flowOfRange(1, 10)
            .map {
                delay(100)
                it
            }
            .flatMapDrop {
                log.trace { "source item=$it" }
                flowOfRange(it * 100, 5)
                    .map { item2 ->
                        delay(20)
                        item2
                    }
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

        flowOfRange(1, 10)
            .map {
                delay(100)
                it
            }
            .flatMapDrop {
                log.trace { "source item=$it" }
                item.value = it
                flowOfRange(it * 100, 5)
                    .map { item2 ->
                        delay(30)
                        item2
                    }
            }
            .take(7)
            .assertResult(
                100, 101, 102, 103, 104,
                300, 301,
            )

        item.value shouldBeEqualTo 3
    }
}
