package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.DockerImageName

class PostgreSQLServer(
    tag: String = TAG,
    useDefaultPort: Boolean = false,
    username: String = "test",
    password: String = "test",
    reuse: Boolean = true,
): PostgreSQLContainer<PostgreSQLServer>(DockerImageName.parse("$IMAGE:$tag")), JdbcServer {

    companion object: KLogging() {
        const val TAG: String = "13"
        const val DEFAULT_PORT: Int = 5432
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
}
