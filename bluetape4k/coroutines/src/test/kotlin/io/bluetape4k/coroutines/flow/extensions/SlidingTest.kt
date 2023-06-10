package io.bluetape4k.coroutines.flow.extensions

import app.cash.turbine.test
import io.bluetape4k.coroutines.flow.eclipse.toFastList
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
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
}
