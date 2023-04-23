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

/**
 * Consul server
 *
 * 참고: [Consul docker image](https://hub.docker.com/_/consul)
 *
 */
class ConsulServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): GenericContainer<ConsulServer>(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "consul"
        const val TAG = "1.7"
        const val NAME = "consul"

        const val DNS_PORT = 8600
        const val HTTP_PORT = 8500
        const val RPC_PORT = 8300

        val EXPORT_PORTS = intArrayOf(DNS_PORT, HTTP_PORT, RPC_PORT)

        operator fun invoke(
            image: String = IMAGE,
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ConsulServer {
            val imageName = DockerImageName.parse(image).withTag(tag)
            return ConsulServer(imageName, useDefaultPort, reuse)
        }
    }

    override val url: String get() = "http://$host:$port"
    override val port: Int get() = getMappedPort(HTTP_PORT)

    val dnsPort: Int get() = getMappedPort(DNS_PORT)
    val httpPort: Int get() = getMappedPort(HTTP_PORT)
    val rpcPort: Int get() = getMappedPort(RPC_PORT)

    init {
        withExposedPorts(*EXPORT_PORTS.toTypedArray())
        withReuse(reuse)
        withLogConsumer(Slf4jLogConsumer(log))

        setWaitStrategy(Wait.forListeningPort())

        if (useDefaultPort) {
            // 위에 withExposedPorts 를 등록했으면, 따로 지정하지 않으면 그 값들을 사용합니다.
            exposeCustomPorts()
        }
    }

    override fun start() {
        super.start()

        val extraProps = mapOf(
            "dns.port" to dnsPort,
            "http.port" to httpPort,
            "rpc.port" to rpcPort
        )
        writeToSystemProperties(NAME, extraProps)
    }

    object Launcher {
        val consul: ConsulServer by lazy {
            ConsulServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
