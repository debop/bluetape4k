package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import java.time.Duration

class DelayTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `delayed flow`() = runTest {
        delayedFlow(1, Duration.ofSeconds(1))
            .assertResult(1)

        delayedFlow(2, 1_000L)
            .assertResult(2)
    }

    @Test
    fun `delayed flow with long time`() = runTest {
        val emitted = atomic(false)

        launch {
            delayedFlow(1, 2_000)
                .collect {
                    it shouldBeEqualTo 1
                    emitted.compareAndSet(expect = false, update = true)
                }
        }

        runCurrent()
        emitted.value.shouldBeFalse()

        advanceTimeBy(1_000)
        emitted.value.shouldBeFalse()

        advanceTimeBy(1_001)
        emitted.value.shouldBeTrue()
    }

    @Test
    fun `delayed flow with duration`() = runTest {
        val emitted = atomic(false)

        launch {
            delayedFlow(1, Duration.ofSeconds(2))
                .collect {
                    it shouldBeEqualTo 1
                    emitted.compareAndSet(expect = false, update = true)
                }
        }

        runCurrent()
        emitted.value.shouldBeFalse()

        advanceTimeBy(1_000)
        emitted.value.shouldBeFalse()

        advanceTimeBy(1_001)
        emitted.value.shouldBeTrue()
    }
}
