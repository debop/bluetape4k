package io.bluetape4k.aws.sqs.model

import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

inline fun SendMessageRequest(initializer: SendMessageRequest.Builder.() -> Unit): SendMessageRequest {
    return SendMessageRequest.builder().apply(initializer).build()
}

fun sendMessageRequestOf(
    queueUrl: String,
    messageBody: String,
    delaySeconds: Int? = null,
): SendMessageRequest = SendMessageRequest {
    queueUrl(queueUrl)
    messageBody(messageBody)
    delaySeconds(delaySeconds)
}

inline fun SendMessageBatchRequestEntry(
    initializer: SendMessageBatchRequestEntry.Builder.() -> Unit,
): SendMessageBatchRequestEntry {
    return SendMessageBatchRequestEntry.builder().apply(initializer).build()
}

fun sendMessageBatchRequestEntryOf(
    id: String,
    messageGroupId: String,
    messageBody: String,
    delaySeconds: Int? = null,
): SendMessageBatchRequestEntry = SendMessageBatchRequestEntry {
    id(id)
    messageGroupId(messageGroupId)
    messageBody(messageBody)
    delaySeconds(delaySeconds)
}
