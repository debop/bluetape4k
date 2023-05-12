package io.bluetapek4.aws.ses

import io.bluetape4k.aws.auth.staticCredentialsProviderOf
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.aws.LocalStackServer
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesClient

abstract class AbstractSesTest {

    companion object: KLogging() {

        @JvmStatic
        private val AwsSES: LocalStackServer by lazy {
            LocalStackServer.Launcher.locakStack.withServices(LocalStackContainer.Service.SES)
        }

        @JvmStatic
        private val endpoint by lazy {
            AwsSES.getEndpointOverride(LocalStackContainer.Service.S3)
        }

        @JvmStatic
        private val credentialsProvider: StaticCredentialsProvider by lazy {
            staticCredentialsProviderOf(AwsSES.accessKey, AwsSES.secretKey)
        }

        @JvmStatic
        private val region: Region
            get() = Region.of(AwsSES.region)

        @JvmStatic
        val client: SesClient by lazy {
            sesClient {
                credentialsProvider(credentialsProvider)
                endpointOverride(endpoint)
                region(region)
            }.apply {
                ShutdownQueue.register(this)
            }
        }

        @JvmStatic
        fun randomString(): String {
            return Fakers.randomString(256, 2048, true)
        }

        const val domain = "example.com"
        const val senderEmail = "from-user@example.com"
        const val receiverEamil = "to-use@example.com"
    }
}
