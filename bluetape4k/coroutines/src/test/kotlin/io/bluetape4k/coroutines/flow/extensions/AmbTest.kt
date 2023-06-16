package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class AmbTest: AbstractFlowTest() {

    @Test
    fun `amb with two flows`() = runTest {
        val flow1 = range(1, 5).onStart { delay(1000) }.log("flow1")
        val flow2 = range(6, 5).onStart { delay(100) }.log("flow2")

        amb(flow1, flow2)
            .assertResult(6, 7, 8, 9, 10)
    }

    @Test
    fun `amb with two flows 2`() = runTest {
        val flow1 = range(1, 5).onStart { delay(100) }.log("flow1")
        val flow2 = range(6, 5).onStart { delay(1000) }.log("flow2")

        amb(flow1, flow2)
            .assertResult(1, 2, 3, 4, 5)
    }

    @Test
    fun `amb with take`() = runTest {
        var counter = 0

        val flow1 = range(1, 5).onEach { delay(100) }.log("flow1")
        val flow2 = range(6, 5)
            .onEach {
                delay(200)
                counter++
            }
            .log("flow2")

        listOf(flow1, flow2)
            .amb()
            .take(3)
            .assertResult(1, 2, 3)

        counter shouldBeEqualTo 0
    }
}
