package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

class MySQL8Server private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean = false,
    configuration: String = "",
    username: String = "test",
    password: String = "test",
    reuse: Boolean = true,
): MySQLContainer<MySQL8Server>(imageName), JdbcServer {

    companion object: KLogging() {
        const val IMAGE = "mysql"
        const val TAG = "8.0"
        const val NAME = "mysql"
        const val PORT: Int = 3306
        const val DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver"

        @JvmStatic
        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = true,
            configuration: String = "",
            username: String = "test",
            password: String = "test",
            reuse: Boolean = true,
        ): MySQL8Server {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return MySQL8Server(imageName, useDefaultPort, configuration, username, password, reuse)
        }

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = true,
            configuration: String = "",
            username: String = "test",
            password: String = "test",
            reuse: Boolean = true,
        ): MySQL8Server {
            return MySQL8Server(imageName, useDefaultPort, configuration, username, password, reuse)
        }
    }

    override fun getDriverClassName(): String = DRIVER_CLASS_NAME
    override val port: Int get() = getMappedPort(PORT)
    override val url: String get() = jdbcUrl

    init {
        if (configuration.isNotBlank()) {
            withConfigurationOverride(configuration)
        }
        addExposedPorts(PORT)
        withUsername(username)
        withPassword(password)

        withReuse(reuse)
        setWaitStrategy(Wait.forListeningPort())

        if (useDefaultPort) {
            exposeCustomPorts(MYSQL_PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME, buildJdbcProperties())
    }

    object Launcher {
        val mysql: MySQL8Server by lazy {
            MySQL8Server().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
