package io.bluetape4k.coroutines

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test

class JobSupportTest {

    companion object: KLogging()

    @Test
    fun `print job hierarchy`() = runSuspendWithIO {

        val job = launch {
            val childJobs = List(10) {
                launch {
                    delay(1000)
                    log.debug { "Child job $it" }
                }
            }
            childJobs.joinAll()
        }

        job.printDebugTree()
    }
}
