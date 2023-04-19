package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.DockerImageName

class PostgreSQLServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean = false,
    username: String = "test",
    password: String = "test",
    reuse: Boolean = true,
): PostgreSQLContainer<PostgreSQLServer>(imageName), JdbcServer {

    companion object: KLogging() {
        const val TAG: String = "14"
        const val DEFAULT_PORT: Int = 5432

        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = true,
            username: String = "test",
            password: String = "test",
            reuse: Boolean = true,
        ): PostgreSQLServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return PostgreSQLServer(imageName, useDefaultPort, username, password, reuse)
        }

        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = true,
            username: String = "test",
            password: String = "test",
            reuse: Boolean = true,
        ): PostgreSQLServer {
            return PostgreSQLServer(imageName, useDefaultPort, username, password, reuse)
        }
    }

    override val url: String get() = jdbcUrl

    init {
        withUsername(username)
        withPassword(password)

        withReuse(reuse)
        withLogConsumer(Slf4jLogConsumer(log))

        if (useDefaultPort) {
            exposeCustomPorts(DEFAULT_PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME, buildJdbcProperties())
    }

    object Launcher {
        val postgres: PostgreSQLServer by lazy {
            PostgreSQLServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
