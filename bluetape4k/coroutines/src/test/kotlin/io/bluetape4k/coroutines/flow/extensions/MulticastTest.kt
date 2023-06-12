package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.TimeUnit

class MulticastTest: AbstractFlowTest() {
    companion object: KLogging()

    @Test
    fun `multicast to one consumer`() = runTest {
        intArrayOf(1, 2, 3, 4, 5).asFlow()
            .publish { shared ->
                shared.filter { it % 2 == 0 }
            }
            .assertResult(2, 4)
    }

    @Test
    @Disabled("collector 가 2개여야 작동합니다")
    fun `publish to multiple consumers`() = runTest {
        intArrayOf(1, 2, 3, 4, 5).asFlow()
            .publish { shared ->
                mergeFlows(shared.filter { it % 2 == 1 }, shared.filter { it % 2 == 0 })
            }
            .assertResult(1, 2, 3, 4, 5)
    }

    @Test
    @Disabled("collector 가 2개여야 작동합니다")
    fun `publish multiple consumer custom merge`() = runTest {
        intArrayOf(1, 2, 3, 4, 5).asFlow()
            .publish { shared ->
                mergeFlows(shared.filter { it % 2 == 1 }, shared.filter { it % 2 == 0 })
            }
            .assertResult(1, 2, 3, 4, 5)
    }

    @Test
    fun `multicast multiple consumers custom merge`() = runTest {
        intArrayOf(1, 2, 3, 4, 5).asFlow()
            .publish(2) { shared ->
                mergeFlows(shared.filter { it % 2 == 1 }, shared.filter { it % 2 == 0 })
            }
            .assertResult(1, 2, 3, 4, 5)
    }

    @Test
    fun `replay one consumer`() = runTest {
        intArrayOf(1, 2, 3, 4, 5).asFlow()
            .replay { shared ->
                shared.filter { it % 2 == 0 }
            }
            .assertResult(2, 4)
    }

    @Test
    fun `replay multiple consumers`() = runTest {
        arrayOf(1, 2, 3, 4, 5).asFlow()
            .replay { shared ->
                mergeFlows(shared.filter { it % 2 == 1 }, shared.filter { it % 2 == 0 })
            }
            .assertResult(1, 3, 5, 2, 4)
    }

    @Test
    fun `replay size bound`() = runTest {
        intArrayOf(1, 2, 3, 4, 5).asFlow()
            .replay(2) { shared ->
                shared.filter { it % 2 == 0 }.concatWithEx(shared)
            }
            .assertResult(2, 4, 4, 5)    // filter: 2, 4   || concatWith : 4, 5
    }

    @Test
    fun `replay time bound`() = runTest {
        val timeout = Duration.ofMinutes(1)

        intArrayOf(1, 2, 3, 4, 5).asFlow()
            .onEach { delay(100) }
            .replay(timeout) { shared ->
                shared.filter { it % 2 == 0 }.concatWithEx(shared)
            }
            .assertResult(2, 4, 1, 2, 3, 4, 5) // filter : 2, 4 || concatWith : 1,2,3,4,5
    }

    @Test
    fun `replay size and time bound`() = runTest {
        val timeout = Duration.ofMinutes(1)

        intArrayOf(1, 2, 3, 4, 5).asFlow()
            .replay(2, timeout) { shared ->
                shared.filter { it % 2 == 0 }.concatWithEx(shared)
            }
            .assertResult(2, 4, 4, 5)    // filter: 2, 4   || concatWith : 4, 5
    }

    @Test
    fun `replay size and time and custom time source bound`() = runTest {

        val timeout = Duration.ofMinutes(1)
        val timeSource: (TimeUnit) -> Long = { System.currentTimeMillis() }

        intArrayOf(1, 2, 3, 4, 5).asFlow()
            .replay(2, timeout, timeSource) { shared ->
                shared.filter { it % 2 == 0 }.concatWithEx(shared)
            }
            .assertResult(2, 4, 4, 5)    // filter: 2, 4   || concatWith : 4, 5
    }
}
