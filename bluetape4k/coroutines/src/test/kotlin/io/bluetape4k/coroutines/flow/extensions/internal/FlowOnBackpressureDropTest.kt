package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.flowOfRange
import io.bluetape4k.coroutines.flow.extensions.onBackpressureDrop
import io.bluetape4k.coroutines.tests.assertResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FlowOnBackpressureDropTest {

    @Test
    fun `drop backpressure items`() = runTest {
        flowOfRange(0, 10)
            .onEach { delay(100) }
            .buffer()
            .onBackpressureDrop()
            // .buffer(2) // buffering 하면 drop을 하지 않습니다.
            .map {
                delay(130)
                it
            }
            .assertResult(0, 2, 4, 6, 8)
    }
}
