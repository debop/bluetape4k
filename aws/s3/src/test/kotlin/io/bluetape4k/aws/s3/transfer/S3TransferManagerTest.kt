package io.bluetape4k.aws.s3.transfer

import io.bluetape4k.aws.s3.AbstractS3Test
import io.bluetape4k.io.deleteIfExists
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import kotlinx.coroutines.future.await
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.util.*

class S3TransferManagerTest: AbstractS3Test() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
    }

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `upload and download text by transfer manager`() = runSuspendWithIO {
        val key = UUID.randomUUID().toString()
        val content = randomString()

        val upload = transferManager.uploadByteArray(BUCKET_NAME, key, content.toUtf8Bytes())

        val completedUpload = upload.completionFuture().await()
        completedUpload.response().eTag().shouldNotBeEmpty()

        val download = transferManager.downloadAsByteArray(BUCKET_NAME, key)

        val completedDownload = download.completionFuture().await()

        val downloadContent = completedDownload.result().asByteArray().toUtf8String()
        downloadContent shouldBeEqualTo content
    }

    @ParameterizedTest(name = "upload/download by transfer manager: {0}")
    @MethodSource("getImageNames")
    fun `upload and download file by transfer manager`(filename: String) = runSuspendWithIO {
        val key = "transfer/$filename"
        val path = "$IMAGE_PATH/$filename"
        val file = File(path)
        file.exists().shouldBeTrue()

        // Upload file by S3TransferManager
        val upload = transferManager.uploadFile(BUCKET_NAME, key, file.toPath())
        val completedUpload = upload.completionFuture().await()
        completedUpload.response().eTag().shouldNotBeEmpty()

        // TempDir 에 파일을 다운로드 한다
        val downloadFile = File(tempDir, filename)
        val downloadPath = downloadFile.toPath()

        val download = transferManager.downloadFile(BUCKET_NAME, key, downloadPath)
        download.completionFuture().await()

        log.debug { "downloadFile=$downloadFile, size=${downloadFile.length()}" }
        downloadFile.exists().shouldBeTrue()
        // downloadFile.length() shouldBeEqualTo file.length()
        downloadFile.deleteIfExists()
    }
}
