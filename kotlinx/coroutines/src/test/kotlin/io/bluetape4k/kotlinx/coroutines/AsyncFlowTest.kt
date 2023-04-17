package io.bluetape4k.kotlinx.coroutines

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random

class AsyncFlowTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    private val dispatcher = newFixedThreadPoolContext(4, "async")

    @RepeatedTest(REPEAT_SIZE)
    fun `asyncFlow with custom dispatcher`() = runTest {
        val results = mutableListOf<Int>()

        List(100) { it + 1 }.asFlow()
            .asAsyncFlow(dispatcher) {
                delay(Random.nextLong(10))
                log.trace { "Started $it" }
                it
            }
            .asyncMap {
                delay(Random.nextLong(10))
                it * it / it
            }
            .asyncCollect {
                // 순서대로 들어와야 한다
                results.lastOrNull()?.let { prev -> it shouldBeEqualTo prev + 1 }
                results.add(it)
            }

        results shouldBeEqualTo List(100) { it + 1 }
    }
}
