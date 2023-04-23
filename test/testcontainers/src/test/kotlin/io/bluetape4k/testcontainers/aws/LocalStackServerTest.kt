package io.bluetape4k.testcontainers.aws

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.testcontainers.containers.Network
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Execution(ExecutionMode.SAME_THREAD)
class LocalStackServerTest {

    companion object: KLogging()

    @Test
    fun `run S3 Service`() {
        LocalStackServer().withServices(LocalStackContainer.Service.S3).use { server ->
            server.start()

            // AWS SDK V2 사용
            val credentialProvider = server.getCredentialProvider()

            val s3 = S3Client.builder()
                .endpointOverride(server.getEndpointOverride(LocalStackContainer.Service.S3))
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(credentialProvider)
                .build()

            s3.createBucket(CreateBucketRequest.builder().bucket("foo").build())
            Thread.sleep(100)

            val putRequest = PutObjectRequest.builder()
                .bucket("foo")
                .key("bar")
                .build()
            s3.putObject(putRequest, RequestBody.fromString("baz"))
            Thread.sleep(100)

            val getRequest = GetObjectRequest.builder()
                .bucket("foo")
                .key("bar")
                .build()
            val content = s3.getObjectAsBytes(getRequest).asUtf8String()
            content shouldBeEqualTo "baz"
        }
    }

    @Test
    fun `run multiple services with custom network`() {
        val network = Network.newNetwork()

        LocalStackServer()
            .withNetwork(network)
            .withNetworkAliases("notthis", "localstack")
            .withServices(LocalStackContainer.Service.S3, LocalStackContainer.Service.CLOUDWATCHLOGS)
            .use { server ->
                server.start()
                server.isRunning.shouldBeTrue()
            }
    }
}
