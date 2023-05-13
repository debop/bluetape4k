package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.amb
import io.bluetape4k.coroutines.flow.extensions.ambFlowOf
import io.bluetape4k.coroutines.flow.extensions.flowOfRange
import io.bluetape4k.coroutines.tests.assertResult
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class FlowAmbIterableTest {

    @Test
    fun `amb with two flows`() = runTest {
        val flow1 = flowOfRange(1, 5).onStart { delay(1000) }
        val flow2 = flowOfRange(6, 5).onStart { delay(100) }

        ambFlowOf(flow1, flow2)
            .assertResult(6, 7, 8, 9, 10)
    }

    @Test
    fun `amb with two flows 2`() = runTest {
        val flow1 = flowOfRange(1, 5).onStart { delay(100) }
        val flow2 = flowOfRange(6, 5).onStart { delay(1000) }

        ambFlowOf(flow1, flow2)
            .assertResult(1, 2, 3, 4, 5)
    }

    @Test
    fun `amb with take`() = runTest {
        val counter = atomic(0)

        val flow1 = flowOfRange(1, 5).onEach { delay(100) }
        val flow2 = flowOfRange(6, 5)
            .onEach {
                delay(200)
                counter.incrementAndGet()
            }

        listOf(flow1, flow2)
            .amb()
            .take(3)
            .assertResult(1, 2, 3)

        counter.value shouldBeEqualTo 0
    }
}
