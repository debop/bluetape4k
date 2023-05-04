package io.bluetape4k.aws.s3

import io.bluetape4k.io.deleteIfExists
import io.bluetape4k.io.utils.Resourcex
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8String
import kotlinx.coroutines.future.await
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.util.*

@Execution(ExecutionMode.CONCURRENT)
class S3AsyncClientExtensionsTest: AbstractS3Test() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
    }

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `put and get s3 object`() = runSuspendWithIO {
        val key = UUID.randomUUID().toString()
        val content = randomString()

        val response = s3AsyncClient.putAsString(BUCKET_NAME, key, content).await()
        response.eTag().shouldNotBeNull()
        log.debug { "put response=$response" }

        val downContent = s3Client.getAsString(BUCKET_NAME, key)

        downContent shouldBeEqualTo content
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `upload and download as byte array`() = runSuspendWithIO {
        val key = UUID.randomUUID().toString()
        val filepath = "files/product_type.csv"
        val bytes = Resourcex.getBytes(filepath)

        val response = s3AsyncClient.putAsByteArray(BUCKET_NAME, key, bytes).await()
        response.eTag().shouldNotBeNull()

        val download = s3Client.getAsByteArray(BUCKET_NAME, key)
        download.toUtf8String() shouldBeEqualTo bytes.toUtf8String()
    }

    @ParameterizedTest(name = "upload/download {0}")
    @MethodSource("getImageNames")
    fun `upload and download binary file`(filename: String) = runSuspendWithIO {
        val key = UUID.randomUUID().toString()
        val path = "$IMAGE_PATH/$filename"
        val file = File(path)
        file.exists().shouldBeTrue()

        val response = s3AsyncClient.putAsFile(BUCKET_NAME, key, file).await()
        response.eTag().shouldNotBeNull()

        val downloadFile = tempDir.resolve(filename)
        val response2 = s3Client.getAsFile(BUCKET_NAME, key, downloadFile)
        response2.eTag().shouldNotBeNull()

        log.debug { "downloadFile=$downloadFile, size=${downloadFile.length()}" }
        downloadFile.exists().shouldBeTrue()
        downloadFile.length() shouldBeEqualTo file.length()
        downloadFile.deleteIfExists()
    }
}
