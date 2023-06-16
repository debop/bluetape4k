package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertError
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FlowFromSuspendTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `flow from suspend emits values`() = runTest {
        var count = 0L
        val flow = flowFromSuspend {
            delay(count)
            count
        }

        flow.assertResult(0)

        count++
        flow.assertResult(1)

        count++
        flow.assertResult(2)
    }

    @Test
    fun `flow from suspend with exception`() = runTest {
        val exception = RuntimeException("Boom!")

        flowFromSuspend<Int> { throw exception }
            .assertError<RuntimeException>()

        flowFromSuspend<Int> { throw exception }
            .materialize()
            .assertResult(Event.Error(exception))
    }

    @Test
    fun `flow from suspend with value supplier raise exception`() = runTest {
        val exception = RuntimeException("Boom!")

        flowFromSuspend<Int> { throw exception }
            .assertError<RuntimeException>()
    }
}
