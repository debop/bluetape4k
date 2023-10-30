package io.bluetape4k.testcontainers.storage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode


@Execution(ExecutionMode.SAME_THREAD)
class ElasticsearchOssTest {

    companion object: KLogging()

    @Test
    fun `launch elasticsearch oss version`() {
        ElasticsearchOss.Launcher.elasticsearchOss.use { es ->
            log.debug { "Elasticsearch OSS URL: ${es.url}" }
            es.isRunning.shouldBeTrue()
        }
    }

    @Test
    fun `launch elasticsearch oss version with default port`() {
        ElasticsearchOss(useDefaultPort = true).use { es ->
            es.start()
            log.debug { "Elasticsearch OSS URL: ${es.url}" }
            es.isRunning.shouldBeTrue()
            es.port shouldBeEqualTo ElasticsearchServer.PORT
        }
    }
}
