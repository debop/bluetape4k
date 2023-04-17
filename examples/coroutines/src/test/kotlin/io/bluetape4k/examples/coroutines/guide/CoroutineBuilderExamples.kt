package io.bluetape4k.examples.coroutines.guide

import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test

class CoroutineBuilderExamples {

    companion object: KLogging()

    @Test
    fun `job example`() = runSuspendTest {
        val job = launch(Dispatchers.Default) {
            delay(1000)
        }
        job.join()
    }

    @Test
    fun `async example`() = runSuspendTest {
        val task: Deferred<Long> = async {
            // do something stuff
            42L
        }
        task.await()
    }
}
