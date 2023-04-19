package io.bluetape4k.testcontainers.massage

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
 * Docker를 이용하여 [nats](http://nats.io)를 구동해주는 container 입니다.
 *
 * 참고: [Nats official images](https://hub.docker.com/_/nats?tab=description&page=1&ordering=last_updated)
 *
 * ```
 * // start nats server by docker
 * val nats = NatsServer().apply { start() }
 * ```
 */
class NatsServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): GenericContainer<NatsServer>(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "nats"
        const val TAG = "2.9"
        const val NAME = "nats"

        val NATS_PORTS: Array<Int> = arrayOf(4222, 6222, 8222)

        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): NatsServer {
            return NatsServer(imageName, useDefaultPort, reuse)
        }

        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): NatsServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return NatsServer(imageName, useDefaultPort, reuse)
        }
    }

    override val url: String
        get() = "$NAME://$host:$port"

    init {
        withExposedPorts(*NATS_PORTS)
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
        writeToSystemProperties(NAME)
    }

    object Launcher {
        val nats: NatsServer by lazy {
            NatsServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
