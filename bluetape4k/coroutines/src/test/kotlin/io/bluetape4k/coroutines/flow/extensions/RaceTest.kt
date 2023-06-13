package io.bluetape4k.coroutines.flow.extensions

import app.cash.turbine.test
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import kotlin.coroutines.cancellation.CancellationException

/**
 * 참고: [race] 는 [amb] 와 같습니다.
 */
class RaceTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `race zero`() = runTest {
        emptyList<Flow<Int>>().race().test { awaitComplete() }
        race(emptyFlow<Int>(), emptyFlow()).test { awaitComplete() }
    }

    @Test
    fun `race single flow`() = runTest {
        listOf(flowOf(1, 2, 3))
            .race()
            .assertResult(1, 2, 3)
    }

    @Test
    fun `race 2 flow without delay`() = runTest {
        race(
            flowOf(1, 2).log(1),
            flowOf(3, 4).log(2)
        ).assertResult(1, 2)
    }

    @Test
    fun `race 3 flow with delay`() = runTest {
        listOf(
            flowOf(1, 2).onEach { delay(200L) }.log(1),
            flowOf(3, 4).onEach { delay(100L) }.log(2),
            flowOf(5, 6).onEach { delay(50L) }.log(3),
        )
            .race()
            .test {
                awaitItem() shouldBeEqualTo 5
                awaitItem() shouldBeEqualTo 6
                awaitComplete()
            }
    }

    @Test
    fun `race 2 flow with start delay`() = runTest {
        listOf(
            flowOf(1, 2, 3).onStart { delay(100L) }.log(1),
            flowOf(2, 3, 4).onStart { delay(200L) }.log(2),
        )
            .race()
            .test {
                awaitItem() shouldBeEqualTo 1
                awaitItem() shouldBeEqualTo 2
                awaitItem() shouldBeEqualTo 3
                awaitComplete()
            }
    }

    @Test
    fun `race with complete`() = runTest {
        listOf(
            flow<Int> { delay(100); }.log(1),
            flow { delay(200); emit(1) }.log(2)
        )
            .race()
            .test {
                awaitComplete()
            }

        listOf(
            flow { delay(200); emit(1) }.log(1),
            flow<Int> { delay(100) }.log(2),
        )
            .race()
            .test {
                awaitComplete()
            }
    }

    @Test
    fun `race with failure in upstream`() = runTest {
        listOf(
            flow {
                delay(100)
                emit(1)
                delay(500)
                throw RuntimeException("Boom!")
            }.log(1),

            flow {
                delay(500)
                emit(2)
                delay(500)
                emit(4)
            }.log(2)
        )
            .race()
            .test {
                awaitItem() shouldBeEqualTo 1
                awaitError()
            }


        listOf(
            flow {
                delay(1000)
                emit(1)
                delay(500)
            }.log(1),

            flow<Int> {
                delay(500)
                throw RuntimeException("Boom!")
            }.log(2)
        )
            .race()
            .test {
                awaitError()
            }
    }

    @Test
    fun `race with take`() = runTest {
        listOf(
            flowOf(1).log(1),
            flowOf(2).log(2)
        )
            .race()
            .take(1)
            .test {
                awaitItem() shouldBeEqualTo 1
                awaitComplete()
            }

        listOf(
            flow {
                emit(1)
                throw RuntimeException("Boom!")
            }.onStart { delay(100) }.log(1),
            flowOf(2).onStart { delay(200) }.log(2)
        )
            .race()
            .take(1)
            .test {
                awaitItem() shouldBeEqualTo 1
                awaitComplete()
            }
    }

    @Test
    fun `race with cancellation`() = runTest {
        val message = "test"

        listOf(
            flow {
                delay(50)
                emit(1)
                throw CancellationException(message)
            }.log("flow1"),

            flow {
                delay(100)
                emit(2)
                emit(3)
            }.log("flow2")
        )
            .race()
            .test {
                awaitItem() shouldBeEqualTo 1
                awaitError().message shouldBeEqualTo message
            }
    }
}
