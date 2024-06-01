package io.bluetape4k.aws.sqs.model

import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

/**
 * [SendMessageRequest] 를 생성합니다.
 *
 * @param initializer [SendMessageRequest.Builder]를 이용하여 [SendMessageRequest]를 초기화하는 람다입니다.
 */
inline fun sendMessageRequest(
    initializer: SendMessageRequest.Builder.() -> Unit,
): SendMessageRequest {
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
    delaySeconds?.run { delaySeconds(delaySeconds) }
    initializer()
}

/**
 * [SendMessageBatchRequestEntry] 를 생성합니다.
 *
 * @param initializer [SendMessageBatchRequestEntry.Builder]를 이용하여 [SendMessageBatchRequestEntry]를 초기화하는 람다입니다.
 */
inline fun sendMessageBatchRequestEntry(
    initializer: SendMessageBatchRequestEntry.Builder.() -> Unit,
): SendMessageBatchRequestEntry {
    return SendMessageBatchRequestEntry.builder().apply(initializer).build()
}

/**
 * Build [SendMessageBatchRequestEntry]
 *
 * @param id                An identifier for the message in this batch.
 * @param messageGroupId    An identifier for the group of messages in this batch.
 * @param messageBody       The message to send.
 * @param delaySeconds      The length of time, in seconds, for which to delay a specific message.
 * @param initializer       The lambda to initialize the builder.
 * @receiver            The builder to build the request.
 * @return            [SendMessageBatchRequestEntry] 인스턴스
 */
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
    delaySeconds?.run { delaySeconds(this) }
    initializer()
}
