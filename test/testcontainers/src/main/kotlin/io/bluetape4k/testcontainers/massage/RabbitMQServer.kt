package io.bluetape4k.testcontainers.massage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName

/**
 * RabbitMQ server
 *
 * 참고: [rabbitmq docker official images](https://hub.docker.com/_/rabbitmq/tags)
 *
 * @param dockerImageName
 * @param useDefaultPort
 * @param reuse
 */
class RabbitMQServer private constructor(
    dockerImageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): RabbitMQContainer(dockerImageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "rabbitmq"
        const val TAG = "3.9"

        const val NAME = "rabbitmq"

        const val AMQP_PORT = 5672
        const val AMQPS_PORT = 5671
        const val RABBITMQ_HTTP_PORT = 15672
        const val RABBITMQ_HTTPS_PORT = 15671

        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): RabbitMQServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return RabbitMQServer(imageName, useDefaultPort, reuse)
        }

        operator fun invoke(
            dockerImageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): RabbitMQServer {
            return RabbitMQServer(dockerImageName, useDefaultPort, reuse)
        }
    }

    override val url: String get() = "amqp://$host:$port"
    override val port: Int get() = getMappedPort(AMQP_PORT)
    val amqpPort: Int get() = getMappedPort(AMQP_PORT)
    val amqpsPort: Int get() = getMappedPort(AMQPS_PORT)
    val rabbitmqHttpPort: Int get() = getMappedPort(RABBITMQ_HTTP_PORT)
    val rabbitmqHttpsPort: Int get() = getMappedPort(RABBITMQ_HTTPS_PORT)

    init {
        addExposedPorts(AMQP_PORT, AMQPS_PORT, RABBITMQ_HTTP_PORT, RABBITMQ_HTTPS_PORT)
        withReuse(reuse)

        // wait strategy 는 RabbitMQContainer 생성자에서 설정합니다.

        if (useDefaultPort) {
            // 위에 withExposedPorts 를 등록했으면, 따로 지정하지 않으면 그 값들을 사용합니다.
            exposeCustomPorts(AMQP_PORT, AMQPS_PORT, RABBITMQ_HTTP_PORT, RABBITMQ_HTTPS_PORT)
        }
    }

    override fun start() {
        super.start()

        val props = mapOf(
            "amqp.port" to amqpPort,
            "amqps.port" to amqpsPort,
            "rabbitmq.http.port" to rabbitmqHttpPort,
            "rabbitmq.https.port" to rabbitmqHttpsPort
        )
        writeToSystemProperties(NAME, props)
    }

    object Launcher {
        val rabbitMQ by lazy {
            RabbitMQServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
