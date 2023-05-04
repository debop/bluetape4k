package io.bluetape4k.aws.s3.examples

import io.bluetape4k.aws.s3.AbstractS3Test
import io.bluetape4k.aws.s3.model.listBucketsRequest
import io.bluetape4k.aws.s3.putAsString
import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.s3.model.RequestPayer
import java.util.*

class BasicExamples: AbstractS3Test() {

    companion object: KLogging()

    @Test
    fun `모든 bucket 을 조회합니다`() {
        val response = s3Client.listBuckets(listBucketsRequest())

        val buckets = response.buckets()
        buckets.forEach { bucket ->
            log.debug { "Bucket=${bucket.name()}" }
        }
        buckets.shouldNotBeEmpty()
    }

    @Test
    fun `bucket의 모든 object를 조회합니다`() {
        val bucket = UUID.randomUUID().encodeBase62().lowercase()
        createBucketsIfNotExists(bucket)

        val keys = List(5) { UUID.randomUUID().encodeBase62() }.sorted()
        keys.forEach { key ->
            s3Client.putAsString(bucket, key, randomString()) {
                it.requestPayer(RequestPayer.REQUESTER)
            }
        }

        val response = s3Client.listObjectsV2 { it.bucket(bucket) }
        val objects = response.contents()

        objects.forEach {
            log.debug { "key=${it.key()}, size=${it.size()}, owner=${it.owner()}" }
        }
        objects.map { it.key() }.sorted() shouldBeEqualTo keys
    }
}
