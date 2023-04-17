package io.bluetape4k.examples.coroutines.guide

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.logging.trace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.random.Random

class FlowExamples {

    companion object: KLogging()

    @Nested
    inner class Flow35 {

        private fun events(): Flow<Int> =
            (1..3).asFlow().onEach { delay(Random.nextLong(100)) }

        @Test
        fun `collect without any code`() = runTest {
            events()
                .onEach { log.trace { "Event: $it" } }
                .onCompletion { error ->
                    log.debug { "Completed" }
                    error?.run { log.error(this) { "Error occurred." } }
                }
                .collect()

            log.debug { "Done!" }
        }
    }

    @Nested
    inner class Flow36 {
        private fun events(): Flow<Int> =
            (1..3).asFlow().onEach { delay(Random.nextLong(100)) }

        @Test
        fun `launch flow in a separate coroutine scope`() = runTest {
            events()
                .onEach { log.trace { "Event: $it" } }
                .onCompletion { error ->
                    log.debug { "Completed" }
                    error?.run { log.error(this) { "Error occurred." } }
                }
                // .flowOn(Dispatchers.IO)
                .launchIn(this)   // CoroutineScope 내의 다음 작업과 동시에 진행된다

            log.debug { "Done!" }
        }
    }
}
