package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResultSet
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertFailsWith

class MapParallelTest: AbstractFlowTest() {

    companion object: KLogging()

    private val parallelism = 4

    private val dispatcher = newFixedThreadPoolContext(parallelism, "flowext")

    @Test
    fun `mapParallel with dispatcher`() = runTest {
        val ranges = flowRangeOf(1, 20)

        ranges
            .onEach { delay(10) }.log("source")
            .buffer()
            .mapParallel(parallelism = parallelism, context = dispatcher) {
                // log.trace { "map parallel: $it" }
                delay(Random.nextLong(10))
                it
            }
            .log("mapParallel")
            .assertResultSet(ranges.toList())
    }

    @Test
    fun `mapParallel with exception`() = runTest {
        val ranges = flowRangeOf(1, 20)
        val error = RuntimeException("Boom!")

        assertFailsWith<RuntimeException> {
            ranges
                .log("source")
                .mapParallel(parallelism = parallelism, context = dispatcher) {
                    log.trace { "map parallel: $it" }
                    delay(Random.nextLong(10))
                    if (it == 3) throw error
                    else it
                }
                .log("mapParallel")
                .collect()
        }
    }
}
