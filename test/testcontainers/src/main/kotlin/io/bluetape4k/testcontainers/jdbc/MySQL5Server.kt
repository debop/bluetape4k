package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

class MySQL5Server private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean = true,
    configuration: String = "",
    username: String = "test",
    password: String = "test",
    reuse: Boolean = true,
): MySQLContainer<MySQL5Server>(imageName), JdbcServer {

    companion object: KLogging() {
        const val TAG = "5.7"
        const val DEFAULT_PORT: Int = 3306
        const val DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver"

        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = true,
            configuration: String = "",
            username: String = "test",
            password: String = "test",
            reuse: Boolean = true,
        ): MySQL5Server {
            val imageName = DockerImageName.parse(NAME).withTag(tag)
            return MySQL5Server(imageName, useDefaultPort, configuration, username, password, reuse)
        }

        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = true,
            configuration: String = "",
            username: String = "test",
            password: String = "test",
            reuse: Boolean = true,
        ): MySQL5Server {
            return MySQL5Server(imageName, useDefaultPort, configuration, username, password, reuse)
        }
    }

    override fun getDriverClassName(): String = DRIVER_CLASS_NAME
    override val url: String get() = jdbcUrl

    init {
        if (configuration.isNotBlank()) {
            withConfigurationOverride(configuration)
        }
        withUsername(username)
        withPassword(password)

        withReuse(reuse)
        withLogConsumer(Slf4jLogConsumer(log))
        setWaitStrategy(Wait.forListeningPort())

        if (useDefaultPort) {
            exposeCustomPorts(DEFAULT_PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME, buildJdbcProperties())
    }
}