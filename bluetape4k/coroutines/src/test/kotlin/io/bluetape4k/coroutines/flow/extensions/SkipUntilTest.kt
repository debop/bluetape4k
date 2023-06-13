package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertFailure
import io.bluetape4k.coroutines.tests.assertResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class SkipUntilTest: AbstractFlowTest() {

    @Test
    fun `skip until`() = runTest {
        // -------1-------2-------3
        // -----------|
        flowOf(1, 2, 3)
            .onEach { delay(100) }
            .skipUntil(delayedFlow(150))
            .assertResult(2, 3)

        // dropUntil 은 skipUntil 과 같은 기능을 합니다.
        flowOf(1, 2, 3)
            .onEach { delay(100) }
            .dropUntil(delayedFlow(150))
            .assertResult(2, 3)
    }

    @Test
    fun `skip until with never flow`() = runTest {
        flowOf(1, 2, 3, 4)
            .skipUntil(neverFlow())
            .assertResult()
    }

    @Test
    fun `skip until with empty flow`() = runTest {
        flowOf(1, 2, 3, 4)
            .skipUntil(emptyFlow())
            .assertResult(1, 2, 3, 4)
    }

    @Test
    fun `skip until with failure upstream`() = runTest {
        // 01-------------2X
        // -------100
        val source = flow {
            emit(0)
            emit(1)

            delay(20)
            emit(2)
            throw RuntimeException("Boom!")
        }

        val notifier = flowOf(100).onEach { delay(10) }

        source
            .skipUntil(notifier)
            .onEach {
                it shouldBeEqualTo 2
            }
            .assertFailure<Int, RuntimeException>(2)
    }
}
