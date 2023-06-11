package io.bluetape4k.coroutines.flow.extensions

import app.cash.turbine.test
import io.bluetape4k.coroutines.flow.eclipse.toFastList
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeLessOrEqualTo
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test

class SlidingTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `sliding flow`() = runTest {
        val slidingCounter = atomic(0)
        val slidingCount by slidingCounter
        val slidingSize = 5

        val sliding = (1..20).asFlow()
            .sliding(slidingSize)
            .onEach { slide ->
                log.trace { "slide=$slide" }
                slide.size shouldBeLessOrEqualTo slidingSize
                slidingCounter.incrementAndGet()
            }
            .toList()

        slidingCount shouldBeEqualTo 20
        sliding shouldHaveSize 20
        sliding.last() shouldBeEqualTo listOf(20)
    }

    @Test
    fun `sliding flow with remaining`() = runTest {
        val slidingCounter = atomic(0)
        val slidingSize = 3

        val sliding = (1..20).asFlow()
            .sliding(slidingSize)
            .onEach { slide ->
                log.trace { "slide=$slide" }
                slide.size shouldBeLessOrEqualTo slidingSize
                slidingCounter.incrementAndGet()
            }
            .toList()

        slidingCounter.value shouldBeEqualTo 20
        sliding shouldHaveSize 20
        sliding.last() shouldBeEqualTo listOf(20)
    }

    @Test
    fun `buffered sliding - 버퍼링을 하면서 sliding 합니다`() = runTest {
        val flow = flowOf(1, 2, 3, 4, 5)

        val sliding = flow
            .bufferedSliding(3)
            .onEach { log.trace { it } }

        sliding.test {
            awaitItem() shouldBeEqualTo listOf(1)
            awaitItem() shouldBeEqualTo listOf(1, 2)
            awaitItem() shouldBeEqualTo listOf(1, 2, 3)
            awaitItem() shouldBeEqualTo listOf(2, 3, 4)
            awaitItem() shouldBeEqualTo listOf(3, 4, 5)
            awaitItem() shouldBeEqualTo listOf(4, 5)
            awaitItem() shouldBeEqualTo listOf(5)
            awaitComplete()
        }

        sliding.toFastList() shouldHaveSize 7
    }


    @Test
    fun `sliding with cancellation`() = runTest {
        flowOfRange(0, 10)
            .sliding(4)
            .take(2)
            .test {
                awaitItem() shouldBeEqualTo listOf(0, 1, 2, 3)
                awaitItem() shouldBeEqualTo listOf(1, 2, 3, 4)
                awaitComplete()
            }
    }

    @Test
    fun `sliding with mutable shared flow`() = runTest {
        val flow = MutableSharedFlow<Int>(extraBufferCapacity = 64)
        val results = mutableListOf<List<Int>>()

        val job1 = flow.sliding(3)
            .onEach {
                results += it
                if (it == listOf(1, 2, 3)) {
                    flow.tryEmit(4)
                }
            }.launchIn(this)

        val job2 = launch {
            flow.tryEmit(1)
            flow.tryEmit(2)
            flow.tryEmit(3)
        }

        advanceUntilIdle()
        job1.cancel()
        job2.cancel()

        results shouldBeEqualTo listOf(
            listOf(1, 2, 3),
            listOf(2, 3, 4)
        )
    }
}
