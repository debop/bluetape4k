package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

/**
 * MySQL 5.7 공식 이미지는 arm64를 지원하지 않습니다.
 * `biarms/mysql:5.7` 이미지를 사용합니다.
 *
 * 참고: [MySQL 5.7 Does Not Have an Official Docker Image on ARM/M1 Mac](https://betterprogramming.pub/mysql-5-7-does-not-have-an-official-docker-image-on-arm-m1-mac-e55cbe093d4c)
 *
 * @constructor
 *
 * @param imageName
 * @param useDefaultPort
 * @param configuration
 * @param username
 * @param password
 * @param reuse
 */
class MySQL5Server private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    configuration: String,
    username: String,
    password: String,
    reuse: Boolean,
): MySQLContainer<MySQL5Server>(imageName), JdbcServer {

    companion object: KLogging() {
        const val IMAGE = "biarms/mysql"
        const val TAG = "5.7"
        const val NAME = "mysql"
        const val PORT: Int = 3306
        const val DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver"

        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = true,
            configuration: String = "",
            username: String = "test",
            password: String = "test",
            reuse: Boolean = true,
        ): MySQL5Server {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag).asCompatibleSubstituteFor(NAME)
            return invoke(imageName, useDefaultPort, configuration, username, password, reuse)
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
        addExposedPorts(PORT)

        withUsername(username)
        withPassword(password)

        withReuse(reuse)
        // withLogConsumer(Slf4jLogConsumer(log))
        setWaitStrategy(Wait.forListeningPort())

        if (useDefaultPort) {
            exposeCustomPorts(PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME, buildJdbcProperties())
    }

    object Launcher {
        val mysql: MySQL5Server by lazy {
            MySQL5Server().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
