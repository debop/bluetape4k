package io.bluetape4k.testcontainers.infrastructure

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

class JaegerServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): GenericContainer<JaegerServer>(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "jaegertracing/all-in-one"
        const val NAME = "jaeger"
        const val TAG = "1"

        const val ZIPKIN_PORT = 9411
        const val FRONTEND_PORT = 16686
        const val CONFIG_PORT = 5778
        const val THRIFT_PORT = 14268

        val EXPOSED_PORT = intArrayOf(ZIPKIN_PORT, FRONTEND_PORT, CONFIG_PORT, THRIFT_PORT)

        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): JaegerServer {
            return JaegerServer(imageName, useDefaultPort, reuse)
        }

        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): JaegerServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return JaegerServer(imageName, useDefaultPort, reuse)
        }
    }

    override val url: String get() = "http://$host:$port"
    override val port: Int get() = getMappedPort(FRONTEND_PORT)
    val frontendPort: Int get() = getMappedPort(FRONTEND_PORT)
    val zipkinPort: Int get() = getMappedPort(ZIPKIN_PORT)
    val configPort: Int get() = getMappedPort(CONFIG_PORT)
    val thriftPort: Int get() = getMappedPort(THRIFT_PORT)

    init {
        withReuse(reuse)
        withExposedPorts(*EXPOSED_PORT.toTypedArray())
        withLogConsumer(Slf4jLogConsumer(log))

        val wait = Wait.forLogMessage(".*Query server started.*\\s", 1)
        setWaitStrategy(wait)

        if (useDefaultPort) {
            exposeCustomPorts(*EXPOSED_PORT)
        }
    }

    override fun start() {
        super.start()

        val extraProps = mapOf<String, Any?>(
            "frontend.port" to frontendPort,
            "zipkin.port" to zipkinPort,
            "config.port" to configPort,
            "thrift.port" to thriftPort,
        )
        writeToSystemProperties(NAME, extraProps)
    }

    object Launcher {
        val jaeger: JaegerServer by lazy {
            JaegerServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
