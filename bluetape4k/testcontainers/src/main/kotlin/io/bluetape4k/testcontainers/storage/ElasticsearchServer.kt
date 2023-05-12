package io.bluetape4k.testcontainers.storage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.utility.DockerImageName

/**
 * Elasticsearch Server
 *
 * Link:
 * * [Elasticsearch Docker images](https://www.docker.elastic.co/r/elasticsearch)
 * * [Elasticsearch OSS Docker Images](https://www.docker.elastic.co/r/elasticsearch/elasticsearch-oss)
 */
class ElasticsearchServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): ElasticsearchContainer(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "docker.elastic.co/elasticsearch/elasticsearch"
        const val TAG = "8.7.0"

        const val OSS_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch-oss"
        const val OSS_TAG = "7.10.2"

        const val NAME = "elasticsearch"

        const val PORT = 9200
        const val TCP_PORT = 9300

        @JvmStatic
        operator fun invoke(
            image: String = OSS_IMAGE,
            tag: String = OSS_TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ElasticsearchServer {
            val imageName = DockerImageName.parse(image).withTag(tag)
            return ElasticsearchServer(imageName, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = getMappedPort(PORT)
    override val url: String get() = httpHostAddress

    init {
        addExposedPorts(PORT, TCP_PORT)
        withReuse(reuse)

        if (useDefaultPort) {
            exposeCustomPorts(PORT, TCP_PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME)
    }

    object Launcher {
        val elasticsearchOss: ElasticsearchServer by lazy {
            ElasticsearchServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
        val elasticsearch: ElasticsearchServer by lazy {
            ElasticsearchServer(IMAGE, TAG).apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
