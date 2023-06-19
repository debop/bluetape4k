package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.TimeUnit

class MulticastTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `multicast to one consumer`() = runTest {
        range(1, 5)
            .publish { shared -> shared.filter { it % 2 == 0 } }.log("filter")
            .assertResult(2, 4)
    }

    @Test
    fun `publish to multiple consumers`() = runTest {
        range(1, 5)
            .publish(2) { shared ->
                mergeFlows(
                    shared.filter { it % 2 == 1 }.log("odd"),
                    shared.filter { it % 2 == 0 }.log("even")
                )
            }
            .assertResult(1, 2, 3, 4, 5)

        // expectedCollectors 가 2개여야 작동합니다
        range(1, 5)
            .publish { shared ->
                mergeFlows(
                    shared.filter { it % 2 == 1 }.log("odd"),
                    shared.filter { it % 2 == 0 }.log("even")
                )
            }
            .assertResult()
    }

    @Test
    fun `publish multiple consumer custom merge`() = runTest {
        range(1, 5)
            .publish(2) { shared ->
                mergeFlows(
                    shared.filter { it % 2 == 1 }.log("odd"),
                    shared.filter { it % 2 == 0 }.log("even")
                )
            }
            .assertResult(1, 2, 3, 4, 5)

        range(1, 5)
            .publish { shared ->
                mergeFlows(
                    shared.filter { it % 2 == 1 }.log("odd"),
                    shared.filter { it % 2 == 0 }.log("even")
                )
            }
            .assertResult()
    }

    @Test
    fun `multicast multiple consumers custom merge`() = runTest {
        range(1, 5)
            .publish(2) { shared ->
                mergeFlows(
                    shared.filter { it % 2 == 1 }.log("odd"),
                    shared.filter { it % 2 == 0 }.log("even")
                )
            }
            .assertResult(1, 2, 3, 4, 5)
    }

    @Test
    fun `replay one consumer`() = runTest {
        range(1, 5)
            .replay { shared ->
                shared.filter { it % 2 == 0 }.log("filtered")
            }
            .assertResult(2, 4)
    }

    @Test
    fun `replay multiple consumers`() = runTest {
        range(1, 5)
            .replay { shared ->
                mergeFlows(
                    shared.filter { it % 2 == 1 }.log("odd"),
                    shared.filter { it % 2 == 0 }.log("even")
                )
            }
            .assertResult(1, 3, 5, 2, 4)
    }

    @Test
    fun `replay size bound`() = runTest {
        range(1, 5)
            .replay(2) { shared ->
                shared
                    .log("filter")                          // 1,2,3,4,5
                    .filter { it % 2 == 0 }                       // 2, 4
                    .concatWith(shared.log("replay 2"))     // 4, 5 (replay : 마지막 2개)
            }
            .assertResult(2, 4, 4, 5)    // filter: 2, 4   || concatWith : 4, 5
    }

    @Test
    fun `replay time bound`() = runTest {
        val timeout = Duration.ofMinutes(1)

        range(1, 5)
            .onEach { delay(100) }
            .replay(timeout) { shared ->
                shared
                    .log("filter")  // 1,2,3,4,5
                    .filter { it % 2 == 0 }              // 2, 4
                    .concatWith(shared.log("replay timeout[$timeout]"))  // 1,2,3,4,5 (replay : timeout 만)
            }
            .assertResult(2, 4, 1, 2, 3, 4, 5) // filter : 2, 4 || concatWith : 1,2,3,4,5
    }

    @Test
    fun `replay size and time bound`() = runTest {
        val timeout = Duration.ofMinutes(1)

        range(1, 5)
            .replay(2, timeout) { shared ->
                shared
                    .log("filter")  // 1,2,3,4,5
                    .filter { it % 2 == 0 }              // 2, 4
                    .concatWith(shared.log("replay 2"))  // 4, 5 (replay : 마지막 2개)
            }
            .assertResult(2, 4, 4, 5)    // filter: 2, 4   || concatWith : 4, 5
    }

    @Test
    fun `replay size and time and custom time source bound`() = runTest {
        val timeout = Duration.ofMinutes(1)
        val timeSource: (TimeUnit) -> Long = { System.currentTimeMillis() }

        range(1, 5)
            .replay(2, timeout, timeSource) { shared ->
                shared
                    .log("filter")
                    .filter { it % 2 == 0 }                  // 2, 4
                    .concatWith(shared.log("replay 2"))  // 4, 5 (replay : 마지막 2개)
            }
            .assertResult(2, 4, 4, 5)    // filter: 2, 4   || concatWith : 4, 5
    }
}