package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.CockroachContainer
import org.testcontainers.utility.DockerImageName

class CockroachServer private constructor(
    imageName: DockerImageName,
    username: String,
    password: String,
    useDefaultPort: Boolean,
    reuse: Boolean,
): CockroachContainer(imageName), JdbcServer {

    companion object: KLogging() {
        const val IMAGE = "cockroachdb/cockroach"
        const val TAG: String = "v22.2.8"
        const val NAME = "cockroach"
        const val DB_PORT = 26257
        const val REST_API_PORT = 8080
        const val DATABASE_NAME = "defaultdb"

        @JvmStatic
        operator fun invoke(
            tag: String = TAG,
            username: String = "test",
            password: String = "test",
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): CockroachServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return CockroachServer(imageName, username, password, useDefaultPort, reuse)
        }

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            username: String = "test",
            password: String = "test",
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): CockroachServer {
            return CockroachServer(imageName, username, password, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = getMappedPort(DB_PORT)
    override val url: String get() = jdbcUrl

    init {
        // CockroachContainer에서 이미 수행한다
        // addExposedPorts(REST_API_PORT, DB_PORT)

        withUsername(username)
        withPassword(password)
        withDatabaseName(DATABASE_NAME)

        withReuse(reuse)

        if (useDefaultPort) {
            exposeCustomPorts(REST_API_PORT, DB_PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME, buildJdbcProperties())
    }

    object Launcher {
        val cockroach: CockroachServer by lazy {
            CockroachServer(useDefaultPort = true).apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
