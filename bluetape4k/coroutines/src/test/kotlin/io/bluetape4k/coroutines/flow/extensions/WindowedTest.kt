package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.eclipse.toFastList
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeLessOrEqualTo
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WindowedTest: AbstractFlowTest() {

    companion object: KLogging()

    @Nested
    inner class Windowed {

        @Test
        fun `windowed flow`() = runTest {
            val windowedCounter = atomic(0)
            val windowedSize = 5
            val windowedStep = 1

            val windowed = (1..20).asFlow()
                .windowed(windowedSize, windowedStep)
                .onEach { windowed ->
                    log.trace { "windowed=$windowed" }
                    windowed.size shouldBeLessOrEqualTo windowedSize
                    windowedCounter.incrementAndGet()
                }
                .toFastList()

            windowedCounter.value shouldBeEqualTo 20
            windowed shouldHaveSize 20
            windowed.first() shouldBeEqualTo listOf(1, 2, 3, 4, 5)
            windowed.last() shouldBeEqualTo listOf(20)
        }

        @Test
        fun `windowed flow with remaining`() = runTest {
            val windowedCounter = atomic(0)
            val windowedSize = 5
            val windowedStep = 4

            val windowed = (1..20).asFlow()
                .windowed(windowedSize, windowedStep)
                .onEach { windowed ->
                    log.trace { "windowed=$windowed" }
                    windowed.size shouldBeLessOrEqualTo windowedSize
                    windowedCounter.incrementAndGet()
                }
                .toFastList()

            windowedCounter.value shouldBeEqualTo 5
            windowed shouldHaveSize 5
            windowed.first() shouldBeEqualTo listOf(1, 2, 3, 4, 5)
            windowed.last() shouldBeEqualTo listOf(17, 18, 19, 20)
        }

        @Test
        fun `windowed flow no duplicated`() = runTest {
            val windowedCounter = atomic(0)
            val windowedSize = 5
            val windowedStep = 5

            val windowed = (1..20).asFlow()
                .windowed(windowedSize, windowedStep)
                .onEach { windowed ->
                    log.trace { "windowed=$windowed" }
                    windowed.size shouldBeEqualTo windowedSize
                    windowedCounter.incrementAndGet()
                }
                .toFastList()

            windowedCounter.value shouldBeEqualTo 4
            windowed shouldHaveSize 4
            windowed.first() shouldBeEqualTo listOf(1, 2, 3, 4, 5)
            windowed.last() shouldBeEqualTo listOf(16, 17, 18, 19, 20)
        }
    }

    @Nested
    inner class WindowedFlow {
        @Test
        fun `windowed flow`() = runTest {
            val windowedCounter = atomic(0)
            val windowedSize = 5
            val windowedStep = 1

            val windowed = (1..20).asFlow()
                .windowedFlow(windowedSize, windowedStep)
                .onEach { windowed ->
                    val items = windowed.toList()
                    log.trace { "windowed items=$items" }
                    items.size shouldBeLessOrEqualTo windowedSize
                    windowedCounter.incrementAndGet()
                }
                .toFastList()

            windowedCounter.value shouldBeEqualTo 20
            windowed shouldHaveSize 20
            windowed.first().toList() shouldBeEqualTo listOf(1, 2, 3, 4, 5)
            windowed.last().toList() shouldBeEqualTo listOf(20)
        }

        @Test
        fun `windowed flow with remaining`() = runTest {
            val windowedCounter = atomic(0)
            val windowedSize = 5
            val windowedStep = 4

            val windowed = (1..20).asFlow()
                .windowedFlow(windowedSize, windowedStep)
                .onEach { windowed ->
                    val items = windowed.toList()
                    log.trace { "windowed items=$items" }
                    items.size shouldBeLessOrEqualTo windowedSize
                    windowedCounter.incrementAndGet()
                }
                .toFastList()

            windowedCounter.value shouldBeEqualTo 5
            windowed shouldHaveSize 5
            windowed.first().toList() shouldBeEqualTo listOf(1, 2, 3, 4, 5)
            windowed.last().toList() shouldBeEqualTo listOf(17, 18, 19, 20)
        }

        @Test
        fun `windowed flow no duplicated`() = runTest {
            val windowedCounter = atomic(0)
            val windowedSize = 5
            val windowedStep = 5

            val windowed = (1..20).asFlow()
                .windowedFlow(windowedSize, windowedStep)
                .onEach { windowed ->
                    val items = windowed.toList()
                    log.trace { "windowed items=$items" }
                    items.size shouldBeEqualTo windowedSize
                    windowedCounter.incrementAndGet()
                }
                .toFastList()

            windowedCounter.value shouldBeEqualTo 4
            windowed shouldHaveSize 4
            windowed.first().toList() shouldBeEqualTo listOf(1, 2, 3, 4, 5)
            windowed.last().toList() shouldBeEqualTo listOf(16, 17, 18, 19, 20)
        }
    }
}
