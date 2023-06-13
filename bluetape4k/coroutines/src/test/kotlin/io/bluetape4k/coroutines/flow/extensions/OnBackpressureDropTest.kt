package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class OnBackpressureDropTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `drop backpressure items`() = runTest {
        range(0, 10)
            .onEach {
                log.trace { "source=$it" }
                delay(100L)
            }
            .buffer()
            .onBackpressureDrop()
            // .buffer(2) // buffering 하면 drop을 하지 않습니다.
            .onEach {
                log.trace { "backpressed=$it" }
                delay(130L)
            }
            .assertResult(0, 2, 4, 6, 8)
    }
}
