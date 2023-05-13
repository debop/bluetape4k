package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.concatMapEager
import io.bluetape4k.coroutines.flow.extensions.flowOfRange
import io.bluetape4k.coroutines.tests.assertFailure
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FlowConcatMapEagerTest {

    companion object: KLogging()

    @Test
    fun `concat map eagerly`() = runTest {
        flowOfRange(1, 5)
            .concatMapEager {
                flowOfRange(it * 10, 5).onEach { delay(100) }
            }
            .assertResult(
                10, 11, 12, 13, 14,
                20, 21, 22, 23, 24,
                30, 31, 32, 33, 34,
                40, 41, 42, 43, 44,
                50, 51, 52, 53, 54
            )
    }

    @Test
    fun `concat map with take`() = runTest {
        flowOfRange(1, 5)
            .concatMapEager {
                flowOfRange(it * 10, 5).onEach { delay(100) }
            }
            .take(7)
            .assertResult(
                10, 11, 12, 13, 14,
                20, 21
            )
    }

    @Test
    fun `concat map with crash mapper`() = runTest {
        flowOfRange(1, 5)
            .concatMapEager<Int, Int> {
                throw RuntimeException("Boom!")
            }
            .assertFailure<Int, RuntimeException>()
    }

    @Test
    fun `concat map with crash in flow`() = runTest {
        flowOfRange(1, 5)
            .concatMapEager<Int, Int> {
                flow {
                    throw RuntimeException("Boom!")
                }
            }
            .assertFailure<Int, RuntimeException>()
    }
}
