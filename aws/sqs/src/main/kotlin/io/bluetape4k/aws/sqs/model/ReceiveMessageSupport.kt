package io.bluetape4k.aws.sqs.model

import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

inline fun receiveMessageRequest(initializer: ReceiveMessageRequest.Builder.() -> Unit): ReceiveMessageRequest {
    return ReceiveMessageRequest.builder().apply(initializer).build()
}

fun receiveMessageRequestOf(
    queueUrl: String,
    maxNumber: Int = 3,
    waitTimeSeconds: Int = 30,
    attributeNames: Collection<String>? = null,
    initializer: ReceiveMessageRequest.Builder.() -> Unit = {},
): ReceiveMessageRequest = receiveMessageRequest {
    queueUrl(queueUrl)
    maxNumberOfMessages(maxNumber)
    waitTimeSeconds(waitTimeSeconds)
    attributeNames?.run { messageAttributeNames(this) }
    initializer()
}
