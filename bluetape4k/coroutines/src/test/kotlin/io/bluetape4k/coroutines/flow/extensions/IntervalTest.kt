package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class IntervalTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `interval operator with time millis`() = runTest {
        range(0, 10).log("source")
            .interval(100, 100).log("interval")
            .assertResult(range(0, 10))
    }

    @Test
    fun `interval with default time millis`() = runTest {
        range(0, 10)
            .interval()
            .assertResult(range(0, 10))
    }

    @Test
    fun `interval operator with duration`() = runTest {
        range(0, 10)
            .interval(200.milliseconds, 100.milliseconds)
            .assertResult(range(0, 10))
    }

    @Test
    fun `interval with initial duration`() = runTest {
        range(0, 10)
            .interval(Duration.ZERO)
            .assertResult(range(0, 10))
    }

    @Test
    fun `create interval by millis`() = runTest {
        intervalFlowOf(200, 100)
            .take(20)
            .assertResult(range(0L, 20))
    }

    @Test
    fun `create interval by duration`() = runTest {
        intervalFlowOf(200.milliseconds, 100.milliseconds)
            .take(20)
            .assertResult(range(0L, 20))
    }
}
