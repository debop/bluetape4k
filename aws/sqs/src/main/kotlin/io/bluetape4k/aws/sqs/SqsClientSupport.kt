package io.bluetape4k.aws.sqs

import io.bluetape4k.aws.http.SdkHttpClientProvider
import io.bluetape4k.aws.sqs.model.sendMessageRequestOf
import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.utils.ShutdownQueue
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.SqsClientBuilder
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityBatchResponse
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityResponse
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchResponse
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse
import software.amazon.awssdk.services.sqs.model.DeleteQueueResponse
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.SendMessageBatchResponse
import software.amazon.awssdk.services.sqs.model.SendMessageResponse
import java.net.URI

inline fun sqsClient(initializer: SqsClientBuilder.() -> Unit): SqsClient {
    return SqsClient.builder().apply(initializer).build()
        .apply {
            ShutdownQueue.register(this)
        }
}

fun sqsClientOf(
    endpoint: URI,
    region: Region,
    credentialsProvider: AwsCredentialsProvider,
    initializer: SqsClientBuilder.() -> Unit = {},
): SqsClient = sqsClient {
    endpointOverride(endpoint)
    region(region)
    credentialsProvider(credentialsProvider)

    httpClient(SdkHttpClientProvider.Apache.apacheHttpClient)
    initializer()
}

fun SqsClient.createQueue(queueName: String): String {
    queueName.requireNotBlank("queueName")
    return createQueue { it.queueName(queueName) }.queueUrl()
}

fun SqsClient.listQueues(
    prefix: String? = null,
    nextToken: String? = null,
    maxResults: Int? = null,
): ListQueuesResponse {
    return listQueues {
        it.queueNamePrefix(prefix)
        it.nextToken(nextToken)
        it.maxResults(maxResults)
    }
}

fun SqsClient.getQueueUrl(queueName: String): GetQueueUrlResponse {
    queueName.requireNotBlank("queueName")
    return getQueueUrl { it.queueName(queueName) }
}

fun SqsClient.send(queueUrl: String, messageBody: String): SendMessageResponse {
    return sendMessage(sendMessageRequestOf(queueUrl, messageBody))
}

fun SqsClient.sendBatch(
    queueUrl: String,
    vararg entries: SendMessageBatchRequestEntry,
): SendMessageBatchResponse {
    return sendMessageBatch {
        it.queueUrl(queueUrl)
        it.entries(*entries)
    }
}

fun SqsClient.sendBatch(
    queueUrl: String,
    entries: Collection<SendMessageBatchRequestEntry>,
): SendMessageBatchResponse {
    return sendMessageBatch {
        it.queueUrl(queueUrl)
        it.entries(entries)
    }
}

fun SqsClient.receiveMessages(
    queueUrl: String,
    maxResults: Int? = null,
    requestBuilder: ReceiveMessageRequest.Builder.() -> Unit = {},
): ReceiveMessageResponse {
    return receiveMessage {
        it.queueUrl(queueUrl)
        it.maxNumberOfMessages(maxResults)
        requestBuilder(it)
    }
}

fun SqsClient.changeMessageVisibility(
    queueUrl: String,
    receiptHandle: String? = null,
    visibilityTimeout: Int? = null,
): ChangeMessageVisibilityResponse {
    return changeMessageVisibility {
        it.queueUrl(queueUrl)
        it.receiptHandle(receiptHandle)
        it.visibilityTimeout(visibilityTimeout)
    }
}

fun SqsClient.changeMessageVisibilityBatch(
    queueUrl: String,
    vararg entries: ChangeMessageVisibilityBatchRequestEntry,
): ChangeMessageVisibilityBatchResponse {
    return changeMessageVisibilityBatch {
        it.queueUrl(queueUrl)
        it.entries(*entries)
    }
}

fun SqsClient.changeMessageVisibilityBatch(
    queueUrl: String,
    entries: Collection<ChangeMessageVisibilityBatchRequestEntry>,
): ChangeMessageVisibilityBatchResponse {
    return changeMessageVisibilityBatch {
        it.queueUrl(queueUrl)
        it.entries(entries)
    }
}

fun SqsClient.deleteMessage(
    queueUrl: String,
    receiptHandle: String? = null,
): DeleteMessageResponse {
    return deleteMessage {
        it.queueUrl(queueUrl)
        it.receiptHandle(receiptHandle)
    }
}

fun SqsClient.deleteMessageBatch(
    queueUrl: String,
    vararg entries: DeleteMessageBatchRequestEntry,
): DeleteMessageBatchResponse {
    return deleteMessageBatch {
        it.queueUrl(queueUrl)
        it.entries(*entries)
    }
}

fun SqsClient.deleteMessageBatch(
    queueUrl: String,
    entries: Collection<DeleteMessageBatchRequestEntry>,
): DeleteMessageBatchResponse {
    return deleteMessageBatch {
        it.queueUrl(queueUrl)
        it.entries(entries)
    }
}

fun SqsClient.deleteQueue(queueUrl: String): DeleteQueueResponse {
    return deleteQueue {
        it.queueUrl(queueUrl)
    }
}
