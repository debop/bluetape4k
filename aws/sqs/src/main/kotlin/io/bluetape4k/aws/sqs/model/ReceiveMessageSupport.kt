package io.bluetape4k.aws.sqs.model

import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

inline fun ReceiveMessageRequest(initializer: ReceiveMessageRequest.Builder.() -> Unit): ReceiveMessageRequest {
    return ReceiveMessageRequest.builder().apply(initializer).build()
}

fun receiveMessageRequestOf(
    queueUrl: String,
    maxNumber: Int = 3,
    waitTimeSeconds: Int = 30,
    attributeNames: Collection<String>? = null,
): ReceiveMessageRequest = ReceiveMessageRequest {
    queueUrl(queueUrl)
    maxNumberOfMessages(maxNumber)
    waitTimeSeconds(waitTimeSeconds)
    attributeNames?.run { messageAttributeNames(this) }
}
