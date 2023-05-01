package io.bluetape4k.aws.s3.model

import io.bluetape4k.core.requireNotBlank
import software.amazon.awssdk.services.s3.model.ListObjectsRequest

inline fun listObjectsRequest(
    bucket: String,
    initializer: ListObjectsRequest.Builder.() -> Unit,
): ListObjectsRequest {
    bucket.requireNotBlank("bucket")
    return ListObjectsRequest.builder()
        .bucket(bucket)
        .apply(initializer)
        .build()
}
