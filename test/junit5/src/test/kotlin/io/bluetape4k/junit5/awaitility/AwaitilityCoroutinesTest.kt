package io.bluetape4k.junit5.awaitility

import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import org.amshove.kluent.shouldBeGreaterThan
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test

class AwaitilityCoroutinesTest {

    companion object : KLogging()

    @Test
    fun `await untilSuspending`() = runSuspendTest {
        val start = System.currentTimeMillis()

        await untilSuspending {
            log.trace { "await untilSuspending" }
            System.currentTimeMillis() > start + 1000
        }

        System.currentTimeMillis() shouldBeGreaterThan start + 1000
    }
}
