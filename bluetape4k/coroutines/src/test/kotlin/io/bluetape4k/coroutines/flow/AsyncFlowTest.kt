package io.bluetape4k.coroutines.flow

import io.bluetape4k.concurrent.virtualthread.VT
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random

class AsyncFlowTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
        private const val ITEM_SIZE = 1_000
        private val expectedItems = List(ITEM_SIZE) { it + 1 }
        private const val MAX_DELAY_TIME = 10L
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `asyncFlow with custom dispatcher`() = runTest {

        @OptIn(DelicateCoroutinesApi::class)
        val dispatcher = newFixedThreadPoolContext(16, "asyncflow")

        val results = mutableListOf<Int>()

        expectedItems.asFlow()
            .async(dispatcher) {
                delay(Random.nextLong(MAX_DELAY_TIME))
                log.trace { "Started $it" }
                it
            }
            .map {
                delay(Random.nextLong(MAX_DELAY_TIME))
                it * it / it
            }
            .collect { curr ->
                // 순서대로 들어와야 한다
                results.lastOrNull()?.let { prev -> curr shouldBeEqualTo prev + 1 }
                results.add(curr)
            }

        results shouldBeEqualTo expectedItems
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `asyncFlow with default dispatcher`() = runTest {
        val results = mutableListOf<Int>()

        expectedItems.asFlow()
            .async(Dispatchers.Default) {
                delay(Random.nextLong(MAX_DELAY_TIME))
                log.trace { "Started $it" }
                it
            }
            .map {
                delay(Random.nextLong(MAX_DELAY_TIME))
                it * it / it
            }
            .onEach { curr ->
                // 순서대로 들어와야 한다
                results.lastOrNull()?.let { prev -> curr shouldBeEqualTo prev + 1 }
                results.add(curr)
            }
            .collect()

        results shouldBeEqualTo expectedItems
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `asyncFlow with io dispatcher`() = runTest {
        val results = mutableListOf<Int>()

        expectedItems.asFlow()
            .async(Dispatchers.IO) {
                delay(Random.nextLong(MAX_DELAY_TIME))
                log.trace { "Started $it" }
                it
            }
            .map {
                delay(Random.nextLong(MAX_DELAY_TIME))
                it * it / it
            }
            .onEach { curr ->
                // 순서대로 들어와야 한다
                results.lastOrNull()?.let { prev -> curr shouldBeEqualTo prev + 1 }
                results.add(curr)
            }
            .collect()

        results shouldBeEqualTo expectedItems
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `asyncFlow with virtual thread dispatcher`() = runTest {
        val results = mutableListOf<Int>()

        expectedItems.asFlow()
            .async(Dispatchers.VT) {
                delay(Random.nextLong(MAX_DELAY_TIME))
                log.trace { "Started $it" }
                it
            }
            .map {
                delay(Random.nextLong(MAX_DELAY_TIME))
                it * it / it
            }
            .onEach { curr ->
                // 순서대로 들어와야 한다
                results.lastOrNull()?.let { prev -> curr shouldBeEqualTo prev + 1 }
                results.add(curr)
            }
            .collect()

        results shouldBeEqualTo expectedItems
    }
}
