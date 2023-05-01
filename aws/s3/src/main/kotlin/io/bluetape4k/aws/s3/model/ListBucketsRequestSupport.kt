package io.bluetape4k.aws.s3.model

import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
import software.amazon.awssdk.services.s3.model.ListBucketsRequest

inline fun listBucketsRequest(
    initializer: ListBucketsRequest.Builder.() -> Unit,
): ListBucketsRequest {
    return ListBucketsRequest.builder().apply(initializer).build()
}

fun listBucketsRequestOf(
    overrideConfiguration: AwsRequestOverrideConfiguration,
): ListBucketsRequest {
    return listBucketsRequest {
        overrideConfiguration(overrideConfiguration)
    }
}
