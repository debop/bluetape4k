package io.bluetape4k.testcontainers.storage

import io.bluetape4k.core.LibraryName
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.utility.DockerImageName

/**
 * Elasticsearch Server 를 Docker container 로 실행해주는 클래스입니다.
 *
 * Link: [Elasticsearch Docker images](https://www.docker.elastic.co/r/elasticsearch)
 */
class ElasticsearchServer private constructor(
    imageName: DockerImageName,
    val password: String,
    useDefaultPort: Boolean,
    reuse: Boolean,
): ElasticsearchContainer(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "docker.elastic.co/elasticsearch/elasticsearch"
        const val TAG = "8.9.0"

        const val NAME = "elasticsearch"
        const val DEFAULT_PASSWORD = LibraryName

        const val PORT = 9200
        const val TCP_PORT = 9300

        @JvmStatic
        operator fun invoke(
            image: String = IMAGE,
            tag: String = TAG,
            password: String = DEFAULT_PASSWORD,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ElasticsearchServer {
            val imageName = DockerImageName.parse(image).withTag(tag)
            return ElasticsearchServer(imageName, password, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = getMappedPort(PORT)
    override val url: String get() = httpHostAddress

    init {
        addExposedPorts(PORT, TCP_PORT)
        withReuse(reuse)
        withPassword(password)

        if (useDefaultPort) {
            exposeCustomPorts(PORT, TCP_PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME)
    }

    object Launcher {
        /**
         * 기본 [ElasticsearchServer]를 제공합니다.
         */
        val elasticsearch: ElasticsearchServer by lazy {
            ElasticsearchServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }

        /**
         * Spring Data Elasticsearch 를 사용 할 때 사용할 클라이언트 설정을 제공합니다.
         *
         * @param elasticsearch [ElasticsearchServer] 인스턴스
         * @return Spring Data Elasticsearch에서 제공하는 [ClientConfiguration] 인스턴스
         */
        fun getClientConfiguration(elasticsearch: ElasticsearchServer): ClientConfiguration {
            return ClientConfiguration.builder()
                .connectedTo(elasticsearch.url)
                .usingSsl(elasticsearch.createSslContextFromCa())
                .withBasicAuth("elastic", elasticsearch.password)
                .build()
        }
    }
}
