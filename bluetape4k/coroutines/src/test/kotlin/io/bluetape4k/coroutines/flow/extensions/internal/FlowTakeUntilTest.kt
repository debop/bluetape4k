package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.range
import io.bluetape4k.coroutines.flow.extensions.takeUntil
import io.bluetape4k.coroutines.tests.assertResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Duration

@Deprecated("move TakeUntilTest")
class FlowTakeUntilTest {

    @Test
    fun basic() = runTest {
        range(1, 10)
            .onEach { delay(100) }
            .takeUntil(Duration.ofMillis(550))
            .assertResult(1, 2, 3, 4, 5)

    }

    @Test
    fun untilTakesLonger() = runTest {
        range(1, 10)
            .onEach { delay(100) }
            .takeUntil(Duration.ofMillis(1500))
            .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }
}
