package io.bluetape4k.testcontainers.storage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import io.codenotary.immudb4j.ImmuClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

class ImmudbServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): GenericContainer<ImmudbServer>(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "codenotary/immudb"
        const val TAG = "1.9DOM"
        const val NAME = "immudb"
        const val PORT = 3322

        const val IMMUDB_USER = "immudb"
        const val IMMUDB_PASSWORD = "immudb"
        const val IMMUDB_DATABASE = "defaultdb"

        @JvmStatic
        operator fun invoke(
            image: String = IMAGE,
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ImmudbServer {
            require(tag.isNotBlank()) { "tag must not be blank." }
            val imageName = DockerImageName.parse(image).withTag(tag)
            return invoke(imageName, useDefaultPort, reuse)
        }

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ImmudbServer {
            return ImmudbServer(imageName, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = getMappedPort(PORT)
    override val url: String get() = "http://$host:$port"

    init {
        addExposedPorts(PORT)
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

        val immudb: ImmudbServer by lazy {
            ImmudbServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }

        fun getClient(immudb: ImmudbServer): ImmuClient {
            return ImmuClient.newBuilder()
                .withServerUrl(immudb.host)
                .withServerPort(immudb.port)
                .build()
        }
    }
}

inline fun <T> withImmuClient(immudb: ImmudbServer, block: ImmuClient.() -> T): T {
    val client = ImmudbServer.Launcher.getClient(immudb)
    return try {
        client.openSession(ImmudbServer.IMMUDB_DATABASE, ImmudbServer.IMMUDB_USER, ImmudbServer.IMMUDB_PASSWORD)
        block(client)
    } finally {
        client.closeSession()
    }
}
