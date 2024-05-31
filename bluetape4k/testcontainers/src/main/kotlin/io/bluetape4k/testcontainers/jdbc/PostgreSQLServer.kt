package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class PostgreSQLServer private constructor(
    imageName: DockerImageName,
    username: String,
    password: String,
    useDefaultPort: Boolean,
    reuse: Boolean,
): PostgreSQLContainer<PostgreSQLServer>(imageName), JdbcServer {

    companion object: KLogging() {
        const val IMAGE = "postgres"
        const val TAG: String = "16"
        const val NAME = "postgresql"
        const val PORT = 5432
        const val USERNAME = "test"
        const val PASSWORD = "test"

        const val DRIVER_CLASS_NAME = "org.postgresql.Driver"

        @JvmStatic
        operator fun invoke(
            image: String = IMAGE,
            tag: String = TAG,
            username: String = USERNAME,
            password: String = PASSWORD,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): PostgreSQLServer {
            val imageName = DockerImageName.parse(image).withTag(tag)
            return invoke(imageName, username, password, useDefaultPort, reuse)
        }

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            username: String = USERNAME,
            password: String = PASSWORD,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): PostgreSQLServer {
            return PostgreSQLServer(imageName, username, password, useDefaultPort, reuse)
        }
    }

    override fun getDriverClassName(): String = DRIVER_CLASS_NAME
    override val port: Int get() = getMappedPort(PORT)
    override val url: String get() = jdbcUrl

    init {
        withUsername(username)
        withPassword(password)

        withReuse(reuse)

        if (useDefaultPort) {
            exposeCustomPorts(PORT)
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
