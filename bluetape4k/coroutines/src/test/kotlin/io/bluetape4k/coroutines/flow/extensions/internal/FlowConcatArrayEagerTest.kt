package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.concatArrayEager
import io.bluetape4k.coroutines.flow.extensions.flowOfRange
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class FlowConcatArrayEagerTest {

    companion object: KLogging()

    @Test
    fun `concat two flow items eagerly`() = runTest {
        val state1 = atomic(0)
        val state2 = atomic(0)

        val flow1 = flowOfRange(1, 5)
            .onStart {
                delay(200)
                state1.value = 1
            }.onEach {
                log.debug { "flow1 item=$it" }
            }

        val flow2 = flowOfRange(6, 5)
            .onStart {
                state2.value = state1.value
            }.onEach {
                log.debug { "flow2 item=$it" }
            }

        concatArrayEager(flow1, flow2)
            .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        state1.value shouldBeEqualTo 1
        state2.value shouldBeEqualTo 0  // flow2 부터 consume 한다
    }

    @Test
    fun `concat one flow`() = runTest {
        concatArrayEager(flowOfRange(1, 5))
            .assertResult(1, 2, 3, 4, 5)
    }

    @Test
    fun `concat with take`() = runTest {
        concatArrayEager(
            flowOfRange(1, 5).onStart { delay(100) },
            flowOfRange(6, 5)
        )
            .take(6)
            .assertResult(1, 2, 3, 4, 5, 6)
    }

    @Test
    fun `cancel concat`() = runTest {
        val counter = atomic(0)

        concatArrayEager(
            flowOfRange(1, 5).onEach {
                delay(200)
                counter.incrementAndGet()
            }
        )
            .take(3)
            .assertResult(1, 2, 3)

        delay(1200)
        counter.value shouldBeEqualTo 3
    }
}
