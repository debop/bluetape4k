package io.bluetape4k.testcontainers.storage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.utility.DockerImageName

/**
 * Elasticsearch Open Source 용 Server 를 Docker container 로 실행해주는 클래스입니다.
 *
 * Link: [Elasticsearch OSS Docker Images](https://www.docker.elastic.co/r/elasticsearch/elasticsearch-oss)
 */
class ElasticsearchOss private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): ElasticsearchContainer(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "docker.elastic.co/elasticsearch/elasticsearch-oss"
        const val TAG = "7.10.2"

        const val NAME = "elasticsearch-oss"

        const val PORT = 9200
        const val TCP_PORT = 9300

        @JvmStatic
        operator fun invoke(
            image: String = IMAGE,
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ElasticsearchOss {
            val imageName = DockerImageName.parse(image).withTag(tag)
            return ElasticsearchOss(imageName, useDefaultPort, reuse)
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
        val elasticsearchOss: ElasticsearchOss by lazy {
            ElasticsearchOss().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
