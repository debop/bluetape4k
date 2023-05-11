package io.bluetape4k.examples.coroutines.guide

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CoroutineBuilderExamples {

    companion object: KLogging()

    @Test
    fun `job example`() = runTest {
        val job = launch(Dispatchers.Default) {
            delay(1000)
        }
        job.join()
    }

    @Test
    fun `async example`() = runTest {
        val task: Deferred<Long> = async {
            delay(1000)
            42L
        }
        task.await()
    }
}
