package io.bluetape4k.testcontainers.massage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.PulsarContainer
import org.testcontainers.utility.DockerImageName

class PulsarServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): PulsarContainer(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "apachepulsar/pulsar"
        const val TAG = "3.0.0"
        const val NAME = "pulsar"
        const val PORT = BROKER_PORT
        const val HTTP_PORT = BROKER_HTTP_PORT

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = false,
        ): PulsarServer {
            return PulsarServer(imageName, useDefaultPort, reuse)
        }

        @JvmStatic
        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = false,
        ): PulsarServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return invoke(imageName, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = getMappedPort(PORT)
    override val url: String get() = pulsarBrokerUrl

    val brokerPort: Int get() = getMappedPort(PORT)
    val brokerHttpPort: Int get() = getMappedPort(HTTP_PORT)

    init {
        withReuse(reuse)
        addExposedPorts(PORT, HTTP_PORT)

        if (useDefaultPort) {
            exposeCustomPorts(PORT, HTTP_PORT)
        }
    }

    override fun start() {
        super.start()

        val extraProps = mapOf(
            "broker.url" to pulsarBrokerUrl,
            "broker.port" to brokerPort,
            "broker.http.port" to brokerHttpPort
        )
        writeToSystemProperties(NAME, extraProps)
    }

    object Launcher {
        val pulsar by lazy {
            PulsarServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
