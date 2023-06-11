package io.bluetape4k.coroutines.flow.extensions

import app.cash.turbine.test
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DematerializeTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `dematerialize flow`() = runTest {
        flowOf(1, 2, 3)
            .materialize()
            .dematerialize()
            .test {
                awaitItem() shouldBeEqualTo 1
                awaitItem() shouldBeEqualTo 2
                awaitItem() shouldBeEqualTo 3
                awaitComplete()
            }

        flowOf(
            Event.Value(1),
            Event.Value(2),
            Event.Value(3),
        )
            .dematerialize()
            .test {
                awaitItem() shouldBeEqualTo 1
                awaitItem() shouldBeEqualTo 2
                awaitItem() shouldBeEqualTo 3
                awaitComplete()
            }

        flowOf(
            Event.Value(1),
            Event.Value(2),
            Event.Value(3),
            Event.Complete,
            Event.Value(4),
            Event.Value(5),
            Event.Value(6),
        )
            .dematerialize()
            .test {
                awaitItem() shouldBeEqualTo 1
                awaitItem() shouldBeEqualTo 2
                awaitItem() shouldBeEqualTo 3
                awaitComplete()
            }
    }

    @Test
    fun `dematerialize Event Complete`() = runTest {
        flowOf(Event.Complete).dematerialize().test {
            awaitComplete()
        }
    }

    @Test
    fun `dematerialize Event of Nothing`() = runTest {
        emptyFlow<Event<Nothing>>().dematerialize().test {
            awaitComplete()
        }
    }

    @Test
    fun `dematerialize with exception`() = runTest {
        val ex = RuntimeException("Boom!")

        flowOf(1, 2, 3)
            .concatWith(flow { throw ex })
            .materialize()
            .dematerialize()
            .test {
                awaitItem() shouldBeEqualTo 1
                awaitItem() shouldBeEqualTo 2
                awaitItem() shouldBeEqualTo 3
                awaitError() shouldBeEqualTo ex
            }

        flowOf(1, 2, 3)
            .startWith(flow { throw ex })
            .materialize()
            .dematerialize()
            .test {
                awaitError() shouldBeEqualTo ex
            }

        flowOf(1, 2, 3)
            .concatWith(flow { throw ex }, flowOf(4, 5, 6))
            .materialize()
            .dematerialize()
            .test {
                awaitItem() shouldBeEqualTo 1
                awaitItem() shouldBeEqualTo 2
                awaitItem() shouldBeEqualTo 3
                awaitError() shouldBeEqualTo ex
            }
    }

    @Test
    fun `dematerialize first item is Event Error`() = runTest {
        val ex = RuntimeException("Boom!")

        assertFailsWith<RuntimeException> {
            flowOf(Event.Error(ex)).dematerialize().collect()
        }


        flowOf(Event.Error(ex), Event.Value(1))
            .dematerialize()
            .test {
                awaitError() shouldBeEqualTo ex
            }

        flowOf(Event.Error(ex), Event.Complete)
            .dematerialize()
            .test {
                awaitError() shouldBeEqualTo ex
            }

    }
}
