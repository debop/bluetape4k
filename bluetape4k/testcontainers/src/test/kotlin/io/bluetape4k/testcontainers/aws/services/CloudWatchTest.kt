package io.bluetape4k.testcontainers.aws.services

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.aws.LocalStackServer
import java.net.URI
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient

@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CloudWatchTest {

    companion object: KLogging()

    private val cloudWatch: LocalStackServer by lazy {
        LocalStackServer.Launcher.locakStack
            .withServices(
                LocalStackContainer.Service.CLOUDWATCH,
                LocalStackContainer.Service.CLOUDWATCHLOGS
            )
    }

    private val cloudWatchEndpoint: URI
        get() = cloudWatch.getEndpointOverride(LocalStackContainer.Service.CLOUDWATCH)

    private val cloudWatchLogsEndpoint: URI
        get() = cloudWatch.getEndpointOverride(LocalStackContainer.Service.CLOUDWATCHLOGS)

    private val cloudWatchClient by lazy {
        CloudWatchClient.builder()
            .endpointOverride(cloudWatchEndpoint)
            .region(Region.US_EAST_1)
            .credentialsProvider(cloudWatch.getCredentialProvider())
            .build()
    }

    private val cloudWatchLogsClient by lazy {
        CloudWatchLogsClient.builder()
            .endpointOverride(cloudWatchEndpoint)
            .region(Region.US_EAST_1)
            .credentialsProvider(cloudWatch.getCredentialProvider())
            .build()
    }


    @BeforeAll
    fun setup() {
        cloudWatch.start()
    }

    @AfterAll
    fun cleanup() {
        runCatching { cloudWatchClient.close() }
        runCatching { cloudWatchLogsClient.close() }
        runCatching { cloudWatch.stop() }
    }

    @Test
    @Order(1)
    fun `create client`() {
        cloudWatchClient.shouldNotBeNull()
        cloudWatchLogsClient.shouldNotBeNull()
    }
}
