package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.CockroachContainer
import org.testcontainers.utility.DockerImageName

/**
 * 분산형 RDBMS인 CockroachDB를 테스트용으로 사용할 수 있는 컨테이너를 제공한다.
 * Postgres 와 호환됩니다.
 *
 * 참고: [Cockroach Labs](https://www.cockroachlabs.com/)
 */
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
        ): CockroachServer {
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
        ): CockroachServer {
            return CockroachServer(imageName, username, password, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = getMappedPort(DB_PORT)
    override val url: String get() = jdbcUrl
    override fun getDriverClassName(): String = DRIVER_CLASS_NAME

    val dbPort: Int get() = getMappedPort(DB_PORT)
    val restApiPort: Int get() = getMappedPort(REST_API_PORT)

    init {
        // CockroachContainer에서 이미 수행한다
        // addExposedPorts(REST_API_PORT, DB_PORT)

        withUsername(username)
        withPassword(password)
        withDatabaseName(DATABASE_NAME)

        withReuse(reuse)

        if (useDefaultPort) {
            exposeCustomPorts(DB_PORT, REST_API_PORT)
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
