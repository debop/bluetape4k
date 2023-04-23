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
    username: String,
    password: String,
    useDefaultPort: Boolean,
    reuse: Boolean,
): PostgreSQLContainer<PostgreSQLServer>(imageName), JdbcServer {

    companion object: KLogging() {
        const val TAG: String = "11"

        operator fun invoke(
            tag: String = TAG,
            username: String = "test",
            password: String = "test",
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): PostgreSQLServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return PostgreSQLServer(imageName, username, password, useDefaultPort, reuse)
        }

        operator fun invoke(
            imageName: DockerImageName,
            username: String = "test",
            password: String = "test",
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): PostgreSQLServer {
            return PostgreSQLServer(imageName, username, password, useDefaultPort, reuse)
        }
    }

    override val url: String get() = jdbcUrl
    override val port: Int get() = getMappedPort(POSTGRESQL_PORT)

    init {
        withExposedPorts(POSTGRESQL_PORT)
        withUsername(username)
        withPassword(password)

        withReuse(reuse)
        withLogConsumer(Slf4jLogConsumer(log))

        if (useDefaultPort) {
            exposeCustomPorts(POSTGRESQL_PORT)
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
