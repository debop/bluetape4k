package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.eclipse.toFastList
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random

class MapParallelTest: AbstractFlowTest() {

    companion object: KLogging()

    private val dispatcher = newFixedThreadPoolContext(8, "flowext")

    @RepeatedTest(REPEAT_SIZE)
    fun `map async with dispatcher`() = runTest {
        val ranges = (1..20)
        val results = ranges.asFlow()
            .mapParallel(dispatcher) {
                log.trace { "AsyncMap Started $it" }
                delay(Random.nextLong(3))
                it
            }
            .map {
                log.trace { "Map Completed $it" }
                it
            }
            .toFastList()

        results shouldContainSame ranges
    }
}
