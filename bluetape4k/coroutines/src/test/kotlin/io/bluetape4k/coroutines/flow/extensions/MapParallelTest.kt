package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.eclipse.toFastList
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertFailsWith

class MapParallelTest: AbstractFlowTest() {

    companion object: KLogging()

    private val dispatcher = newFixedThreadPoolContext(4, "flowext")

    @Test
    fun `mapParallel with dispatcher`() = runTest {
        val ranges = (1..20)

        val results = ranges
            .asFlow()
            .onEach { delay(10) }
            .buffer()
            .mapParallel(dispatcher, concurrency = 4) {
                log.trace { "map parallel: $it" }
                delay(Random.nextLong(10))
                it
            }
            .onEach { log.trace { "map completed: $it" } }
            .toFastList()

        results shouldContainSame ranges
    }

    @Test
    fun `mapParallel with exception`() = runTest {
        val ranges = (1..20)
        val error = RuntimeException("Boom!")

        assertFailsWith<RuntimeException> {
            ranges.asFlow()
                .mapParallel(dispatcher, concurrency = 4) {
                    log.trace { "map parallel: $it" }
                    delay(Random.nextLong(10))
                    if (it == 3) throw error
                    else it
                }
                .onEach { log.trace { "result=$it" } }
                .collect()
        }.message shouldBeEqualTo "Boom!"
    }
}
