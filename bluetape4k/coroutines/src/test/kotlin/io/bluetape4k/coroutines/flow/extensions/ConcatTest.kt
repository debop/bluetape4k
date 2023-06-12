package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeFalse
import org.junit.jupiter.api.Test

class ConcatTest: AbstractFlowTest() {

    companion object: KLogging()

    val flow1 = flowOf(1, 2)
    val flow2 = flowOf(3, 4)
    val flow3 = flowOf(5, 6)

    @Test
    fun `concat multiple flows`() = runTest {
        concat(flow1, flow2)
            .assertResult(1, 2, 3, 4)
    }

    @Test
    fun `concat with 3 flows`() = runTest {
        concat(flow1, flow2, flow3)
            .assertResult(1, 2, 3, 4, 5, 6)
    }

    @Test
    fun `concat with`() = runTest {
        flow1.concatWith(flow2)
            .assertResult(1, 2, 3, 4)

        flow1.concatWith(flow2, flow3)
            .assertResult(1, 2, 3, 4, 5, 6)
    }

    @Test
    fun `concat collection of flows`() = runTest {
        listOf(flow1, flow2, flow3)
            .concat()
            .assertResult(1, 2, 3, 4, 5, 6)
    }

    @Test
    fun `startWith items`() = runTest {
        flow1.startWith(0)
            .assertResult(0, 1, 2)

        flow1.startWith(-2, -1, 0)
            .assertResult(-2, -1, 0, 1, 2)
    }

    @Test
    fun `startWith with valueSupplier`() = runTest {
        var i = 1
        var called = false
        val flow = flowOf(2).startWith {
            called = true
            i++
        }

        called.shouldBeFalse()

        flow.assertResult(1, 2)
        flow.assertResult(2, 2)
        flow.assertResult(3, 2)
    }

    @Test
    fun `startWith flows`() = runTest {
        flow3.startWith(flow1, flow2)
            .assertResult(1, 2, 3, 4, 5, 6)
    }

    @Test
    fun `endWith items`() = runTest {
        flow1.endWith(3)
            .assertResult(1, 2, 3)

        flow1.endWith(3, 4)
            .assertResult(1, 2, 3, 4)
    }

    @Test
    fun `endWith flows`() = runTest {
        flow1.endWith(flow2)
            .assertResult(1, 2, 3, 4)

        flow1.endWith(flow2, flow3)
            .assertResult(1, 2, 3, 4, 5, 6)
    }
}
