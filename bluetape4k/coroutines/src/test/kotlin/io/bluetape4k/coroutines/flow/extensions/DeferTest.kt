package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DeferTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `deferred emits values`() = runTest {
        var count = 0L
        val flow = defer {
            delay(count)
            flowOf(count)
        }

        flow.assertResult(0L)

        count++
        flow.assertResult(1L)

        count++
        flow.assertResult(2L)
    }

    @Test
    fun `defer with exception`() = runTest {
        val exception = RuntimeException("Boom!")

        assertFailsWith<RuntimeException> {
            defer<Int> {
                flow { throw exception }
            }.collect()
        }

        defer<Int> {
            flow { throw exception }
        }.materialize().assertResult(Event.Error(exception))
    }

    @Test
    fun `defer with value supplier raise exception`() = runTest {
        val exception = RuntimeException("Boom!")

        assertFailsWith<RuntimeException> {
            defer<Int> { throw exception }.collect()
        }
    }
}
