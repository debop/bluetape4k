package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertError
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class WithLatestFromTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `with other latest item`() = runTest {
        val f1 = flowOf(1, 2, 3, 4)
        val f2 = flowOf("a", "b", "c", "d", "e")

        f2.withLatestFrom(f1)
            .assertResult("a" to 4, "b" to 4, "c" to 4, "d" to 4, "e" to 4)
    }

    @Test
    fun `with other latest item which is null`() = runTest {
        val f1 = flowOf(1, 2, 3, 4, null)
        val f2 = flowOf("a", "b", "c", "d", "e")

        f2.withLatestFrom(f1)
            .assertResult("a" to null, "b" to null, "c" to null, "d" to null, "e" to null)
    }

    @Test
    fun `with other latest with delay`() = runTest {
        val f1 = flowOf(1, 2, 3, 4).onEach { delay(300) }
        val f2 = flowOf("a", "b", "c", "d", "e", "f").onEach { delay(100) }

        f2.withLatestFrom(f1)
            .assertResult("c" to 1, "d" to 1, "e" to 1, "f" to 2)
    }

    @Test
    fun `withLatestFrom with failure upstream`() = runTest {

        flow<Int> { throw RuntimeException("Boom!") }
            .withLatestFrom(neverFlow())
            .assertError<RuntimeException>()

        neverFlow()
            .withLatestFrom(flow<Int> { throw RuntimeException("Boom!") })
            .assertError<RuntimeException>()
    }

    @Test
    fun `withLatestFrom with cancellation`() = runTest {
        flow {
            emit(1)
            throw CancellationException("Boom!")
        }
            .withLatestFrom(emptyFlow<Nothing>())
            .assertError<CancellationException>()

        flowOf(1)
            .withLatestFrom(
                flow {
                    emit("a")
                    throw CancellationException("Boom!")
                }
            )
            .assertResult(1 to "a")
    }
}
