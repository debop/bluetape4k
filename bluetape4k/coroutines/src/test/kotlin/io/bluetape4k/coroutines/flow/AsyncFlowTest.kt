package io.bluetape4k.coroutines.flow

import io.bluetape4k.collections.eclipse.primitives.intArrayList
import io.bluetape4k.collections.eclipse.primitives.intArrayListOf
import io.bluetape4k.collections.eclipse.primitives.lastOrNull
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.newFixedThreadPoolContext
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random

class AsyncFlowTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
        private const val ITEM_SIZE = 1000
        private val expectedItems by lazy { intArrayList(ITEM_SIZE) { it + 1 } }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val dispatcher = newFixedThreadPoolContext(4, "async")

    @RepeatedTest(REPEAT_SIZE)
    fun `asyncFlow with custom dispatcher`() = runSuspendWithIO {
        val results = intArrayListOf()

        expectedItems.asFlow()
            .async(dispatcher) {
                delay(Random.nextLong(3))
                log.trace { "Started $it" }
                it
            }
            .map {
                delay(Random.nextLong(3))
                it * it / it
            }
            .collect { curr ->
                // 순서대로 들어와야 한다
                results.lastOrNull()?.let { prev -> curr shouldBeEqualTo prev + 1 }
                results.add(curr)
            }

        results shouldBeEqualTo expectedItems
    }
}