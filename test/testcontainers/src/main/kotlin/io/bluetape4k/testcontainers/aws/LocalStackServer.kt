package io.bluetape4k.testcontainers.aws

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider

/**
 * LocalStackServer : 'a fully funcational local AWS cloud stack'
 *
 * ```
 * // Run S3 Server
 * val s3Server = LocalStackServer().withServices(Service.S3)
 * s3Server.start()
 * ```
 *
 * ```
 * val server = LocalStackServer()
 *    .withNetwork(network)
 *    .withNetworkAliases("notthis", "localstack")
 *    .withServices(Service.S3, Service.SQS, Service.CLOUDWATCHLOGS)
 *
 * server.start()
 * ```
 *
 */
class LocalStackServer private constructor(
    tag: String,
    useDefaultPort: Boolean,
    reuse: Boolean,
): LocalStackContainer(IMAGE_NAME.withTag(tag)), GenericServer {

    companion object: KLogging() {
        val IMAGE_NAME: DockerImageName = DockerImageName.parse("localstack/localstack")
        val NAME = "localstack"
        val TAG = "2.0"
        val PORT = 4566

        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): LocalStackServer {
            return LocalStackServer(tag, useDefaultPort, reuse)
        }
    }

    override val url: String get() = "http://$host:$port"

    init {
        withExposedPorts(PORT)
        withReuse(reuse)
        withLogConsumer(Slf4jLogConsumer(log))

        if (useDefaultPort) {
            exposeCustomPorts(PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME)
    }

    override fun withServices(vararg services: Service): LocalStackServer = apply {
        super.withServices(*services)
    }

    fun getCredentialProvider(): StaticCredentialsProvider {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(this.accessKey, this.secretKey))
    }


    /**
     * [LocalStackServer]용 Launcher
     */
    object Launcher {
        val locakStack: LocalStackContainer by lazy {
            LocalStackServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
