package io.bluetape4k.testcontainers.llm

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.requireNotBlank
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import io.milvus.client.MilvusServiceClient
import io.milvus.param.ConnectParam
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

/**
 * Milvus server
 *
 * 참고: [nstall Milvus Standalone with Docker Compose](https://milvus.io/docs/install_standalone-docker.md)
 *
 * TODO: 독립적으로 실행하는 것이 아니라서, `milvus-standalone-docker-compose.yml` 파일을
 * Testcontainers [DockerComposeContainer] 를 이용하여 실행하거나,
 * [Gradle docker-compose-plugin](https://github.com/avast/gradle-docker-compose-plugin) 을 이용하여 실행
 *
 * @param imageName
 * @param useDefaultPort
 * @param reuse
 */
class MilvusServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): GenericContainer<MilvusServer>(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "milvusdb/milvus"
        const val TAG = "v2.3.16"
        const val NAME = "milvus"
        const val REST_PORT = 9091      // REST
        const val PORT = 19530          // GRPC

        @JvmStatic
        operator fun invoke(
            image: String = IMAGE,
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): MilvusServer {
            image.requireNotBlank("image")
            tag.requireNotBlank("tag")
            val imageName = DockerImageName.parse(image).withTag(tag)
            return invoke(imageName, useDefaultPort, reuse)
        }

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): MilvusServer {
            return MilvusServer(imageName, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = getMappedPort(PORT)
    override val url: String get() = "$host:$port"

    val connectParam: ConnectParam
        get() = ConnectParam.newBuilder()
            .withHost(host)
            .withPort(port)
            .build()

    init {
        addExposedPorts(PORT, REST_PORT)
        withReuse(reuse)
        withLogConsumer(Slf4jLogConsumer(log))

        setWaitStrategy(Wait.forListeningPort())

        if (useDefaultPort) {
            exposeCustomPorts(PORT, REST_PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME)
    }

    fun getClient(): MilvusServiceClient {
        return MilvusServiceClient(connectParam)
    }

    /**
     * Mulvus Server를 실행해주는 Launcher 입니다.
     */
    object Launcher {

        @JvmStatic
        val milvus: MilvusServer by lazy {
            MilvusServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
