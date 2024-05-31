package io.bluetape4k.testcontainers.infrastructure

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

/**
 * zipkin 서버의 docker image를 testcontainers 환경 하에서 실행하는 클래스입니다.
 *
 * 참고: [Zipkin Docker image](https://hub.docker.com/r/openzipkin/zipkin/tags)
 *
 * @param imageName
 * @param useDefaultPort
 * @param reuse
 */
class ZipkinServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): GenericContainer<ZipkinServer>(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "openzipkin/zipkin"
        const val NAME = "zipkin"
        const val TAG = "3"
        const val PORT = 9411

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ZipkinServer {
            return ZipkinServer(imageName, useDefaultPort, reuse)
        }

        @JvmStatic
        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ZipkinServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return ZipkinServer(imageName, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = getMappedPort(PORT)
    override val url: String get() = "http://$host:$port"

    init {
        addExposedPorts(PORT)
        withReuse(reuse)

        setWaitStrategy(Wait.forListeningPort())

        if (useDefaultPort) {
            exposeCustomPorts(PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME)
    }

    object Launcher {
        val zipkin: ZipkinServer by lazy {
            ZipkinServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }

        val defaultZipkin: ZipkinServer by lazy {
            ZipkinServer(useDefaultPort = true).apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
