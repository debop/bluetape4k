package io.bluetape4k.testcontainers.infrastructure

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import java.time.Duration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.utility.DockerImageName

/**
 * Docker를 이용하여 Prometheus 서버를 수행합니다.
 *
 * 참고: [Prometheus docker image](https://hub.docker.com/r/bitnami/prometheus)
 *
 * ```
 * val prometheusServer = PrometheusServer().apply {
 *     start()
 *     ShutdownQueue.register(this)
 * }
 * ```
 */
class PrometheusServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): GenericContainer<PrometheusServer>(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "bitnami/prometheus"
        const val TAG = "2.43.0"
        const val NAME = "prometheus"

        const val PORT = 9090
        const val PUSHGATEWAY_PORT = 9091
        const val GRAPHITE_EXPORTER_PORT = 9109

        val EXPOSED_PORTS = intArrayOf(PORT, PUSHGATEWAY_PORT, GRAPHITE_EXPORTER_PORT)

        operator fun invoke(
            image: String = IMAGE,
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): PrometheusServer {
            val imageName = DockerImageName.parse(image).withTag(tag)
            return PrometheusServer(imageName, useDefaultPort, reuse)
        }
    }

    override val port get() = getMappedPort(PORT)

    val serverPort: Int get() = getMappedPort(PORT)
    val pushgatewayPort: Int get() = getMappedPort(PUSHGATEWAY_PORT)
    val graphiteExporterPort: Int get() = getMappedPort(GRAPHITE_EXPORTER_PORT)

    init {
        withReuse(reuse)
        withExposedPorts(*EXPOSED_PORTS.toTypedArray())
        withLogConsumer(Slf4jLogConsumer(log))

        val waitStrategy = LogMessageWaitStrategy()
            .withRegEx(".*Server is ready to receive web requests.*")
            .withTimes(1)
            .withStartupTimeout(Duration.ofSeconds(5))

        setWaitStrategy(waitStrategy)

        if (useDefaultPort) {
            // 위에 withExposedPorts 를 등록했으면, 따로 지정하지 않으면 그 값들을 사용합니다.
            exposeCustomPorts()
        }
    }

    override fun start() {
        super.start()

        val extraProps = mapOf<String, Any?>(
            "serverPort" to serverPort,
            "pushgatewayPort" to pushgatewayPort,
            "graphiteExporterPort" to graphiteExporterPort
        )
        writeToSystemProperties(NAME, extraProps)
    }

    object Launch {
        val prometheus: PrometheusServer by lazy {
            PrometheusServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
