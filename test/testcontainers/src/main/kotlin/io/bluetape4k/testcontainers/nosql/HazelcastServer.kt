package io.bluetape4k.testcontainers.nosql

import com.hazelcast.config.RestApiConfig
import com.hazelcast.spi.properties.HazelcastProperty
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

class HazelcastServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): GenericContainer<HazelcastServer>(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "hazelcast/hazelcast"
        const val TAG = "5.2"
        const val NAME = "hazelcast"
        const val PORT = 5701

        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): HazelcastServer {
            return HazelcastServer(imageName, useDefaultPort, reuse)
        }

        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): HazelcastServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return HazelcastServer(imageName, useDefaultPort, reuse)
        }
    }

    private val enabledFeatures = HashSet<HazelcastProperty>()
    private val customProperties = HashSet<String>()
    private lateinit var config: RestApiConfig

    init {
        withExposedPorts(PORT)
        withLogConsumer(Slf4jLogConsumer(log))
        withReuse(reuse)

        setWaitStrategy(Wait.forListeningPort())

        if (useDefaultPort) {
            exposeCustomPorts(PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME)
    }

    fun withHttpHealthCheck() = apply {
        // TODO: deprecated feature 수정 필요
        // enabledFeatures.add(GroupProperty.HTTP_HEALTHCHECK_ENABLED)
    }

    fun withRESTClient() = apply {
        // TODO: deprecated feature 수정 필요
        // enabledFeatures.add(GroupProperty.REST_ENABLED)
    }

    fun withRestApi(config: RestApiConfig) {
        this.config = config
    }

    fun withCustomProperty(property: String) = apply {
        customProperties.add(property)
    }

    override fun configure() {
        super.configure()

        var javaOpts = ""
        if (::config.isInitialized) {
            javaOpts += config.enabledGroups.joinToString(" ") { "-D${it.name}=true" }
        }

        javaOpts += " " + enabledFeatures.joinToString(" ") { "-D${it.name}=true" }
        val customProps = customProperties.joinToString(" ") { "-D$it" }

        log.debug(javaOpts)
        withEnv("JAVA_OPTS", "$javaOpts $customProps")
    }

    fun getRestBaseUrl(): String = "$url/hazelcast/rest"

    object Launcher {
        val hazelcast: HazelcastServer by lazy {
            HazelcastServer()
                .withRESTClient()
                .withHttpHealthCheck()
                .apply {
                    start()
                    ShutdownQueue.register(this)
                }
        }
    }
}
