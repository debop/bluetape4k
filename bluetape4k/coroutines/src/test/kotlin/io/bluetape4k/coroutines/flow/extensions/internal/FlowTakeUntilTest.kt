package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.flowOfRange
import io.bluetape4k.coroutines.flow.extensions.takeUntil
import io.bluetape4k.coroutines.tests.assertResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Duration

class FlowTakeUntilTest {

    @Test
    fun basic() = runTest {
        flowOfRange(1, 10)
            .map {
                delay(100)
                it
            }
            .takeUntil(Duration.ofMillis(550))
            .assertResult(1, 2, 3, 4, 5)

    }

    @Test
    fun untilTakesLonger() = runTest {
        flowOfRange(1, 10)
            .map {
                delay(100)
                it
            }
            .takeUntil(Duration.ofMillis(1500))
            .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }
}
