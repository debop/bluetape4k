package io.bluetape4k.testcontainers.storage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
class ImmudbServerTest {

    companion object: KLogging()

    @Nested
    inner class UseDefaultPort {
        @Test
        fun `create immudb server with default port`() {
            ImmudbServer(useDefaultPort = true).use { immudb ->
                immudb.start()
                immudb.isRunning.shouldBeTrue()
                immudb.port shouldBeEqualTo ImmudbServer.PORT

                verifyGetValue(immudb)
            }
        }
    }

    @Nested
    inner class UseDockerPort {
        @Test
        fun `create immudb server`() {
            ImmudbServer.Launcher.immudb.use { immudb ->
                immudb.isRunning.shouldBeTrue()
                verifyGetValue(immudb)
            }
        }
    }

    private fun verifyGetValue(immudb: ImmudbServer) {
        withImmuClient(immudb) {
            val value = "value1"
            set("test1", value.toUtf8Bytes())
            val entry = verifiedGet("test1")!!
            entry.value.toUtf8String() shouldBeEqualTo value
        }
    }
}
