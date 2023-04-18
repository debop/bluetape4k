package io.bluetape4k.testcontainers.storage

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
class ElasticsearchServerTest {

    companion object: KLogging()

    @Test
    fun `launch elasticsearch oss version`() {
        val ess = ElasticsearchServer.Launcher.elasticsearchOss
        ess.isRunning.shouldBeTrue()
    }

    @Test
    fun `launch elasticsearch oss version with default port`() {
        ElasticsearchServer(useDefaultPort = true).use { ess ->
            ess.start()
            ess.isRunning.shouldBeTrue()
            ess.port shouldBeEqualTo ElasticsearchServer.PORT
        }
    }
}
