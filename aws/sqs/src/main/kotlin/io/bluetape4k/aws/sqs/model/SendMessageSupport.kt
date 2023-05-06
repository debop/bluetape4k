package io.bluetape4k.aws.sqs.model

import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

inline fun sendMessageRequest(initializer: SendMessageRequest.Builder.() -> Unit): SendMessageRequest {
    return SendMessageRequest.builder().apply(initializer).build()
}

fun sendMessageRequestOf(
    queueUrl: String,
    messageBody: String,
    delaySeconds: Int? = null,
    initializer: SendMessageRequest.Builder.() -> Unit = {},
): SendMessageRequest = sendMessageRequest {
    queueUrl(queueUrl)
    messageBody(messageBody)
    delaySeconds(delaySeconds)
    initializer()
}

inline fun sendMessageBatchRequestEntry(
    initializer: SendMessageBatchRequestEntry.Builder.() -> Unit,
): SendMessageBatchRequestEntry {
    return SendMessageBatchRequestEntry.builder().apply(initializer).build()
}

fun sendMessageBatchRequestEntryOf(
    id: String,
    messageGroupId: String,
    messageBody: String,
    delaySeconds: Int? = null,
    initializer: SendMessageBatchRequestEntry.Builder.() -> Unit = {},
): SendMessageBatchRequestEntry = sendMessageBatchRequestEntry {
    id(id)
    messageGroupId(messageGroupId)
    messageBody(messageBody)
    delaySeconds(delaySeconds)
    initializer()
}
