package io.bluetape4k.aws.s3.examples

import io.bluetape4k.aws.s3.AbstractS3Test
import io.bluetape4k.aws.s3.getAsByteArray
import io.bluetape4k.aws.s3.putAsByteArray
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import kotlinx.coroutines.future.await
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import java.util.*

class S3AsyncOps: AbstractS3Test() {

    companion object: KLogging()

    @Test
    fun `put object asynchronously`() = runTest {
        val key = UUID.randomUUID().toString()
        val response = s3AsyncClient
            .putAsByteArray(BUCKET_NAME, key, randomString().toUtf8Bytes())
            .await()


        log.debug { "Put response=$response" }
        response.eTag().shouldNotBeEmpty()
    }

    @Test
    fun `get object asynchronously`() = runTest {
        val key = UUID.randomUUID().toString()
        val value = randomString()

        // Put object
        s3AsyncClient.putAsByteArray(BUCKET_NAME, key, value.toUtf8Bytes()).await()

        // Get object
        val content = s3AsyncClient.getAsByteArray(BUCKET_NAME, key).await()
        content.toUtf8String() shouldBeEqualTo value
    }
}
