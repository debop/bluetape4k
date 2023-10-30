package io.bluetape4k.testcontainers.storage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
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
        ElasticsearchServer.Launcher.elasticsearchOss.use { es ->
            log.debug { "Elasticsearch URL: ${es.url}" }
            es.isRunning.shouldBeTrue()
        }
    }

    @Test
    fun `launch elasticsearch oss version with default port`() {
        ElasticsearchServer(useDefaultPort = true).use { es ->
            es.start()
            log.debug { "Elasticsearch URL: ${es.url}" }
            es.isRunning.shouldBeTrue()
            es.port shouldBeEqualTo ElasticsearchServer.PORT
        }
    }

    @Test
    fun `launch elasticsearch`() {
        ElasticsearchServer.Launcher.elasticsearch.use { es ->
            log.debug { "Elasticsearch URL: ${es.url}" }
            es.isRunning.shouldBeTrue()
        }
    }

    @Test
    fun `launch elasticsearch with default port`() {
        ElasticsearchServer(useDefaultPort = true).use { es ->
            es.start()
            log.debug { "Elasticsearch URL: ${es.url}" }
            es.isRunning.shouldBeTrue()
            es.port shouldBeEqualTo ElasticsearchServer.PORT
        }
    }
}
