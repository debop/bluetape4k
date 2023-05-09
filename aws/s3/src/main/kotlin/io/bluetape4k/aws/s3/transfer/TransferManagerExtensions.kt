package io.bluetape4k.aws.s3.transfer

import io.bluetape4k.aws.s3.model.toAsyncRequestBody
import io.bluetape4k.core.requireNotBlank
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.transfer.s3.S3TransferManager
import software.amazon.awssdk.transfer.s3.model.Download
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest
import software.amazon.awssdk.transfer.s3.model.DownloadRequest
import software.amazon.awssdk.transfer.s3.model.FileDownload
import software.amazon.awssdk.transfer.s3.model.FileUpload
import software.amazon.awssdk.transfer.s3.model.Upload
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest
import software.amazon.awssdk.transfer.s3.model.UploadRequest
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener
import java.nio.file.Path

inline fun <T> S3TransferManager.download(
    responseTransformer: AsyncResponseTransformer<GetObjectResponse, T>,
    @BuilderInference initializer: DownloadRequest.UntypedBuilder.() -> Unit,
): Download<T> {
    return download(downloadRequest(responseTransformer, initializer))
}

fun <T> S3TransferManager.download(
    bucket: String,
    key: String,
    responseTransformer: AsyncResponseTransformer<GetObjectResponse, T>,
    getObjectRequestBuilder: (GetObjectRequest.Builder) -> Unit = {},
): Download<T> {
    bucket.requireNotBlank("bucket")
    key.requireNotBlank("key")

    val request = downloadRequestOf(bucket, key, responseTransformer, getObjectRequestBuilder)
    return download(request)
}

fun S3TransferManager.downloadAsByteArray(
    bucket: String,
    key: String,
    getObjectRequestBuilder: (GetObjectRequest.Builder) -> Unit = {},
): Download<ResponseBytes<GetObjectResponse>> {
    bucket.requireNotBlank("bucket")
    key.requireNotBlank("key")

    return download(
        bucket,
        key,
        AsyncResponseTransformer.toBytes(),
        getObjectRequestBuilder
    )
}

fun S3TransferManager.downloadFile(
    bucket: String,
    key: String,
    objectPath: Path,
    additionalDownloadRequest: (DownloadFileRequest.Builder) -> Unit = { it.addTransferListener(LoggingTransferListener.create()) },
): FileDownload {
    bucket.requireNotBlank("bucket")
    key.requireNotBlank("key")

    return downloadFile { fileRequest ->
        fileRequest.getObjectRequest { gorb ->
            gorb.bucket(bucket)
            gorb.key(key)
        }
        fileRequest.destination(objectPath)
        additionalDownloadRequest(fileRequest)
    }
}

fun S3TransferManager.upload(
    bucket: String,
    key: String,
    asyncRequestBody: AsyncRequestBody,
    additionalUploadRequest: (UploadRequest.Builder) -> Unit = {},
): Upload {
    val request = uploadRequest {
        putObjectRequest {
            it.bucket(bucket)
            it.key(key)
        }
        requestBody(asyncRequestBody)
        additionalUploadRequest(this)
    }
    return upload(request)
}

fun S3TransferManager.uploadByteArray(
    bucket: String,
    key: String,
    content: ByteArray,
    additionalUploadRequest: (UploadRequest.Builder) -> Unit = {},
): Upload {
    val request = uploadRequest {
        putObjectRequest {
            it.bucket(bucket)
            it.key(key)
        }
        requestBody(content.toAsyncRequestBody())
        additionalUploadRequest(this)
    }
    return upload(request)
}

//fun S3TransferManager.uploadFile(
//    bucket: String,
//    key: String,
//    source: Path,
//    additionalUploadRequest: (UploadRequest.Builder) -> Unit = {},
//): Upload {
//    val request = uploadRequest {
//        putObjectRequest {
//            it.bucket(bucket)
//            it.key(key)
//        }
//        requestBody(source.toAsyncRequestBody())
//        additionalUploadRequest(this)
//    }
//    return upload(request)
//}

fun S3TransferManager.uploadFile(
    bucket: String,
    key: String,
    source: Path,
    uploadRequest: (UploadFileRequest.Builder) -> Unit = { it.addTransferListener(LoggingTransferListener.create()) },
): FileUpload {
    val request = UploadFileRequest.builder()
        .putObjectRequest {
            it.bucket(bucket)
            it.key(key)
        }
        .source(source)
        .apply(uploadRequest)
        .build()

    return uploadFile(request)
}
