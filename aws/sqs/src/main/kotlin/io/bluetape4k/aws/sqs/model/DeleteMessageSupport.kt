package io.bluetape4k.aws.sqs.model

import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest

inline fun deleteMessageRequest(initializer: DeleteMessageRequest.Builder.() -> Unit): DeleteMessageRequest {
    return DeleteMessageRequest.builder().apply(initializer).build()
}

fun deleteMessageRequestOf(
    queueUrl: String,
    receiptHandle: String,
): DeleteMessageRequest = deleteMessageRequest {
    queueUrl(queueUrl)
    receiptHandle(receiptHandle)
}

inline fun deleteMessageBatchRequest(
    initializer: DeleteMessageBatchRequest.Builder.() -> Unit,
): DeleteMessageBatchRequest {
    return DeleteMessageBatchRequest.builder().apply(initializer).build()
}

fun deleteMessageBatchRequestOf(
    queueUrl: String,
    entries: Collection<DeleteMessageBatchRequestEntry>,
): DeleteMessageBatchRequest = deleteMessageBatchRequest {
    queueUrl(queueUrl)
    entries(entries)
}

inline fun deleteMessageBatchRequestEntry(
    initializer: DeleteMessageBatchRequestEntry.Builder.() -> Unit,
): DeleteMessageBatchRequestEntry {
    return DeleteMessageBatchRequestEntry.builder().apply(initializer).build()
}

fun deleteMessageBatchRequestEntryOf(
    id: String,
    receiptHandle: String,
): DeleteMessageBatchRequestEntry = deleteMessageBatchRequestEntry {
    id(id)
    receiptHandle(receiptHandle)
}
