package io.bluetape4k.aws.s3

import io.bluetape4k.aws.auth.staticCredentialsProviderOf
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.testcontainers.aws.LocalStackServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.transfer.s3.S3TransferManager
import java.util.concurrent.Executors

abstract class AbstractS3Test {

    companion object: KLogging() {

        const val IMAGE_PATH: String = "./src/test/resources/images"
        const val BUCKET_NAME: String = "test-bucket"
        const val BUCKET_NAME2: String = "test-bucket-2"

        @JvmStatic
        private val AwsS3: LocalStackServer by lazy {
            LocalStackServer.Launcher.locakStack.withServices(LocalStackContainer.Service.S3)
        }

        @JvmStatic
        private val endpoint by lazy {
            AwsS3.getEndpointOverride(LocalStackContainer.Service.S3)
        }

        @JvmStatic
        private val credentialsProvider: StaticCredentialsProvider by lazy {
            staticCredentialsProviderOf(AwsS3.accessKey, AwsS3.secretKey)
        }

        @JvmStatic
        private val region: Region
            get() = Region.of(AwsS3.region)

        @JvmStatic
        protected val faker = Fakers.faker

        @JvmStatic
        protected fun randomString(): String =
            Fakers.randomString(256, 2048)
    }

    val syncClient: S3Client by lazy {
        S3Factory.Sync.create {
            endpointOverride(endpoint)
            region(region)
            credentialsProvider(credentialsProvider)
        }
    }

    val asyncClient: S3AsyncClient by lazy {
        S3Factory.Async.create {
            endpointOverride(endpoint)
            region(region)
            credentialsProvider(credentialsProvider)
        }
    }

    val crtAsyncClient: S3AsyncClient by lazy {
        S3Factory.CrtAsync.create {
            endpointOverride(endpoint)
            region(region)
            credentialsProvider(credentialsProvider)
            futureCompletionExecutor(Executors.newVirtualThreadPerTaskExecutor())
        }
    }

    val transferManager: S3TransferManager by lazy {
        S3Factory.TransferManager.create(
            endpoint,
            region,
            credentialsProvider
        ) {
            this.executor(Executors.newVirtualThreadPerTaskExecutor())
        }
    }

    @BeforeAll
    fun beforeAll() {
        deleteBuckets()
        createBucketsIfNotExists(BUCKET_NAME, BUCKET_NAME2)
    }

    @AfterAll
    fun afterAll() {
        deleteBuckets()
    }

    protected fun deleteBuckets() {
        // 기존 bucket을 삭제한다
        val buckets = syncClient.listBuckets().buckets()

        buckets.forEach { bucket ->
            val listObjects = syncClient.listObjects { it.bucket(bucket.name()) }.contents()
            listObjects
                .map { it.key() }
                .forEach { key ->
                    log.debug { "Delete object... bucket=${bucket.name()}, key=$key" }
                    syncClient.deleteObject { it.bucket(bucket.name()).key(key) }
                }
            log.debug { "Delete bucket... name=${bucket.name()}" }
            syncClient.deleteBucket { it.bucket(bucket.name()) }
        }
    }

    protected fun createBucketsIfNotExists(vararg bucketNames: String) {
        bucketNames.forEach { bucket ->
            if (!syncClient.existsBucket(bucket)) {
                log.debug { "Create bucket... name=$bucket" }
                runCatching {
                    val response = syncClient.createBucket { it.bucket(bucket) }
                    if (response.sdkHttpResponse().isSuccessful) {
                        log.debug { "Create new bucket. name=$bucket, location=${response.location()}" }
                    } else {
                        log.error { "Fail to create bucket. name=$bucket, response=${response.sdkHttpResponse()}" }
                    }
                }
            }
        }
    }

    val IMAGE_PATH: String = "src/test/resources/images"

    protected fun getImageNames(): List<String> {
        return listOf(
            "cafe.jpg",
            "flower.jpg",
            "garden.jpg",
            "landscape.jpg",
            "wabull.jpg",
            "coroutines.pdf",
            "kotlin.key",
        )
    }
}
