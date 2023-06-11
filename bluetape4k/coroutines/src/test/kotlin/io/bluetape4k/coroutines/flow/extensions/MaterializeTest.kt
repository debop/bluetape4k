package io.bluetape4k.coroutines.flow.extensions

import app.cash.turbine.test
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class MaterializeTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `materialize happy case`() = runTest {
        flowOf(1, 2, 3)
            .materialize()
            .test {
                awaitItem() shouldBeEqualTo Event.Value(1)
                awaitItem() shouldBeEqualTo Event.Value(2)
                awaitItem() shouldBeEqualTo Event.Value(3)
                awaitItem() shouldBeEqualTo Event.Complete
                awaitComplete()
            }
    }

    @Test
    fun `materialize with exception`() = runTest {
        val exception = RuntimeException("Boom!")

        flowOf(1, 2, 3)
            .concatWith(flow { throw exception })
            .materialize()
            .test {
                awaitItem() shouldBeEqualTo Event.Value(1)
                awaitItem() shouldBeEqualTo Event.Value(2)
                awaitItem() shouldBeEqualTo Event.Value(3)
                awaitItem() shouldBeEqualTo Event.Error(exception)
                awaitComplete()
            }

        flowOf(1, 2, 3)
            .startWith(flow { throw exception })
            .materialize()
            .test {
                awaitItem() shouldBeEqualTo Event.Error(exception)
                awaitComplete()
            }

        flowOf(1, 2, 3)
            .concatWith(flow { throw exception })
            .concatWith(flowOf(4, 5, 6))
            .materialize()
            .test {
                awaitItem() shouldBeEqualTo Event.Value(1)
                awaitItem() shouldBeEqualTo Event.Value(2)
                awaitItem() shouldBeEqualTo Event.Value(3)
                awaitItem() shouldBeEqualTo Event.Error(exception)
                awaitComplete()
            }
    }

    @Test
    fun `materialize with cancel`() = runTest {
        flowOf(1, 2, 3)
            .take(1)
            .materialize()
            .test {
                awaitItem() shouldBeEqualTo Event.Value(1)
                awaitItem() shouldBeEqualTo Event.Complete
                awaitComplete()
            }

        val exception = RuntimeException("Boom!")
        flowOf(1, 2, 3)
            .concatWith(flow { throw exception })
            .take(3)
            .materialize()
            .test {
                awaitItem() shouldBeEqualTo Event.Value(1)
                awaitItem() shouldBeEqualTo Event.Value(2)
                awaitItem() shouldBeEqualTo Event.Value(3)
                awaitItem() shouldBeEqualTo Event.Complete
                awaitComplete()
            }
    }
}
