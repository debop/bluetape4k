package io.bluetape4k.testcontainers.infrastructure

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.requireNotBlank
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryOneTime
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

class ZookeeperServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean = false,
    reuse: Boolean = true,
): GenericContainer<ZookeeperServer>(imageName), GenericServer {

    companion object: KLogging() {

        const val IMAGE = "zookeeper"
        const val TAG = "3.9.2"
        const val NAME = "zookeeper"
        const val PORT = 2181

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ZookeeperServer {
            return ZookeeperServer(imageName, useDefaultPort, reuse)
        }

        @JvmStatic
        operator fun invoke(
            image: String = IMAGE,
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ZookeeperServer {
            image.requireNotBlank("image")
            tag.requireNotBlank("tag")

            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return invoke(imageName, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = getMappedPort(PORT)
    override val url: String get() = "$host:$port"

    init {
        withExposedPorts(PORT)
        withReuse(reuse)
        waitingFor(Wait.forListeningPort())

        if (useDefaultPort) {
            exposeCustomPorts(PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME)
    }

    object Launcher {
        val zookeeper: ZookeeperServer by lazy {
            ZookeeperServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }

        fun getCuratorFramework(zookeeper: ZookeeperServer): CuratorFramework {
            return curatorFrameworkOf {
                connectString(zookeeper.url)
                retryPolicy(RetryOneTime(100))
                connectionTimeoutMs(3000)
            }
        }
    }
}

inline fun curatorFrameworkOf(
    initializer: CuratorFrameworkFactory.Builder.() -> Unit,
): CuratorFramework {
    return CuratorFrameworkFactory.builder().apply(initializer).build()
}

inline fun <T> withCuratorFramework(zookeeper: ZookeeperServer, block: CuratorFramework.() -> T): T {
    return ZookeeperServer.Launcher.getCuratorFramework(zookeeper).use { curator ->
        curator.start()
        block(curator)
    }
}
