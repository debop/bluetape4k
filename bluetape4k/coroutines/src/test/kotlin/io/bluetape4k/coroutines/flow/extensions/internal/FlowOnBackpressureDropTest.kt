package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.log
import io.bluetape4k.coroutines.flow.extensions.onBackpressureDrop
import io.bluetape4k.coroutines.flow.extensions.range
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@Deprecated("move to OnBackpressureDropTest")
class FlowOnBackpressureDropTest {

    companion object: KLogging()

    @Test
    fun `drop backpressure items`() = runTest {
        range(0, 10)
            .onEach { delay(100L) }
            .log("source")
            .buffer()
            .onBackpressureDrop()
            // .buffer(2) // buffering 하면 drop을 하지 않습니다.
            .onEach { delay(130L) }
            .log("backpressed")
            .assertResult(0, 2, 4, 6, 8)
    }
}
