package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.time.Duration

class IntervalTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `create interval by millis`() = runTest {
        val list = intervalFlowOf(200, 100)
            .take(20)
            .toList()
        list shouldBeEqualTo flowOfLongRange(0L, 20).toList()
    }

    @Test
    fun `create interval by duration`() = runTest {
        val list = intervalFlowOf(Duration.ofMillis(200), Duration.ofMillis(100))
            .take(20)
            .toList()
        list shouldBeEqualTo flowOfLongRange(0L, 20).toList()
    }
}
