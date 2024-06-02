package io.bluetape4k.testcontainers.massage

import io.bluetape4k.core.LibraryName
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
class RedpandaServerTest {

    companion object: KLogging() {
        private const val TOPIC_NAME = "$LibraryName-test-topic-1"
        private const val TOPIC_NAME_CORUTINE = "$LibraryName-test-topic-coroutines-1"
    }

    @Nested
    inner class UseDockerPort {
        @Test
        fun `launch redpanda server`() {
            RedpandaServer().use { redpanda ->
                redpanda.start()
                redpanda.isRunning.shouldBeTrue()

                log.debug { "bootstrapServers=${redpanda.bootstrapServers}" }
                redpanda.bootstrapServers.shouldNotBeEmpty()
            }
        }
    }

    @Nested
    inner class UseDefaultPort {
        @Test
        fun `launch redpanda server with default port`() {
            RedpandaServer(useDefaultPort = true).use { redpanda ->
                redpanda.start()
                redpanda.isRunning.shouldBeTrue()

                log.debug { "bootstrapServers=${redpanda.bootstrapServers}" }
                redpanda.bootstrapServers.shouldNotBeEmpty()
                redpanda.port shouldBeEqualTo RedpandaServer.PORT
            }
        }
    }
}
