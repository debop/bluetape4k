package io.bluetape4k.aws.ses

import io.bluetape4k.aws.auth.staticCredentialsProviderOf
import io.bluetape4k.aws.http.SdkAsyncHttpClientProvider
import io.bluetape4k.aws.http.SdkHttpClientProvider
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.aws.LocalStackServer
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesAsyncClient
import software.amazon.awssdk.services.ses.SesClient

abstract class AbstractSesTest {

    companion object: KLogging() {

        @JvmStatic
        protected val awsSES: LocalStackServer by lazy {
            LocalStackServer.Launcher.locakStack.withServices(LocalStackContainer.Service.SES)
        }

        @JvmStatic
        protected val endpoint by lazy {
            awsSES.getEndpointOverride(LocalStackContainer.Service.SES)
        }

        @JvmStatic
        protected val credentialsProvider: StaticCredentialsProvider by lazy {
            staticCredentialsProviderOf(awsSES.accessKey, awsSES.secretKey)
        }

        @JvmStatic
        protected val region: Region
            get() = Region.of(awsSES.region)

        @JvmStatic
        protected val client: SesClient by lazy {
            sesClient {
                credentialsProvider(credentialsProvider)
                endpointOverride(endpoint)
                region(region)
                httpClient(SdkHttpClientProvider.Apache.apacheHttpClient)
            }.apply {
                ShutdownQueue.register(this)
            }
        }

        @JvmStatic
        protected val asyncClient: SesAsyncClient by lazy {
            sesAsyncClient {
                credentialsProvider(credentialsProvider)
                endpointOverride(endpoint)
                region(region)
                httpClient(SdkAsyncHttpClientProvider.Netty.nettyNioAsyncHttpClient)
            }.apply {
                ShutdownQueue.register(this)
            }
        }

        @JvmStatic
        protected val faker = Fakers.faker

        @JvmStatic
        protected fun randomString(): String {
            return Fakers.randomString(256, 2048)
        }

        const val domain = "example.com"
        const val senderEmail = "from-user@example.com"
        const val receiverEamil = "to-use@example.com"
    }
}
