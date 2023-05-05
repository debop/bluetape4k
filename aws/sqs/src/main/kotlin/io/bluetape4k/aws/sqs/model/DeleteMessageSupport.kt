package io.bluetape4k.aws.sqs.model

import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest

inline fun DeleteMessageRequest(initializer: DeleteMessageRequest.Builder.() -> Unit): DeleteMessageRequest {
    return DeleteMessageRequest.builder().apply(initializer).build()
}

fun deleteMessageRequestOf(
    queueUrl: String,
    receiptHandle: String,
): DeleteMessageRequest = DeleteMessageRequest {
    queueUrl(queueUrl)
    receiptHandle(receiptHandle)
}

inline fun DeleteMessageBatchRequest(
    initializer: DeleteMessageBatchRequest.Builder.() -> Unit,
): DeleteMessageBatchRequest {
    return DeleteMessageBatchRequest.builder().apply(initializer).build()
}

fun deleteMessageBatchRequestOf(
    queueUrl: String,
    entries: Collection<DeleteMessageBatchRequestEntry>,
): DeleteMessageBatchRequest = DeleteMessageBatchRequest {
    queueUrl(queueUrl)
    entries(entries)
}

inline fun DeleteMessageBatchRequestEntry(
    initializer: DeleteMessageBatchRequestEntry.Builder.() -> Unit,
): DeleteMessageBatchRequestEntry {
    return DeleteMessageBatchRequestEntry.builder().apply(initializer).build()
}

fun deleteMessageBatchRequestEntryOf(
    id: String,
    receiptHandle: String,
): DeleteMessageBatchRequestEntry = DeleteMessageBatchRequestEntry {
    id(id)
    receiptHandle(receiptHandle)
}
