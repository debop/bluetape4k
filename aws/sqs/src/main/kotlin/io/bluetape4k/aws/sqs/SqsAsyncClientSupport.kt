package io.bluetape4k.aws.sqs

import io.bluetape4k.aws.sqs.model.sendMessageRequestOf
import io.bluetape4k.core.requireNotBlank
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.core.client.config.ClientAsyncConfiguration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.SqsAsyncClientBuilder
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
import java.util.concurrent.CompletableFuture

inline fun SqsAsyncClient(initializer: SqsAsyncClientBuilder.() -> Unit): SqsAsyncClient {
    return SqsAsyncClient.builder().apply(initializer).build()
}

fun sqsAsyncClientOf(
    endpoint: URI,
    region: Region,
    credentialsProvider: AwsCredentialsProvider,
    asyncConfiguration: ClientAsyncConfiguration? = null,
): SqsAsyncClient = SqsAsyncClient {
    endpointOverride(endpoint)
    region(region)
    credentialsProvider(credentialsProvider)
    asyncConfiguration?.run { asyncConfiguration(this) }
}

fun SqsAsyncClient.createQueue(queueName: String): CompletableFuture<String> {
    queueName.requireNotBlank("queueName")
    return createQueue { it.queueName(queueName) }
        .thenApply { it.queueUrl() }
}

fun SqsAsyncClient.listQueues(
    prefix: String? = null,
    nextToken: String? = null,
    maxResults: Int? = null,
): CompletableFuture<ListQueuesResponse> {
    return listQueues {
        it.queueNamePrefix(prefix)
        it.nextToken(nextToken)
        it.maxResults(maxResults)
    }
}

fun SqsAsyncClient.getQueueUrl(
    queueName: String,
    queueOwnerAWSAccountId: String? = null,
): CompletableFuture<GetQueueUrlResponse> {
    queueName.requireNotBlank("queueName")
    return getQueueUrl {
        it.queueName(queueName)
        it.queueOwnerAWSAccountId(queueOwnerAWSAccountId)
    }
}

fun SqsAsyncClient.send(
    queueUrl: String,
    messageBody: String,
    delaySeconds: Int? = null,
): CompletableFuture<SendMessageResponse> {
    return sendMessage(sendMessageRequestOf(queueUrl, messageBody, delaySeconds))
}

fun SqsAsyncClient.sendBatch(
    queueUrl: String,
    vararg entries: SendMessageBatchRequestEntry,
): CompletableFuture<SendMessageBatchResponse> {
    return sendMessageBatch {
        it.queueUrl(queueUrl)
        it.entries(*entries)
    }
}

fun SqsAsyncClient.sendBatch(
    queueUrl: String,
    entries: Collection<SendMessageBatchRequestEntry>,
): CompletableFuture<SendMessageBatchResponse> {
    return sendMessageBatch {
        it.queueUrl(queueUrl)
        it.entries(entries)
    }
}

fun SqsAsyncClient.receiveMessages(
    queueUrl: String,
    maxResults: Int? = null,
    requestBuilder: ReceiveMessageRequest.Builder.() -> Unit = {},
): CompletableFuture<ReceiveMessageResponse> {
    return receiveMessage {
        it.queueUrl(queueUrl)
        it.maxNumberOfMessages(maxResults)
        requestBuilder(it)
    }
}

fun SqsAsyncClient.changeMessageVisibility(
    queueUrl: String,
    receiptHandle: String? = null,
    visibilityTimeout: Int? = null,
): CompletableFuture<ChangeMessageVisibilityResponse> {
    return changeMessageVisibility {
        it.queueUrl(queueUrl)
        it.receiptHandle(receiptHandle)
        it.visibilityTimeout(visibilityTimeout)
    }
}

fun SqsAsyncClient.changeMessageVisibilityBatch(
    queueUrl: String,
    vararg entries: ChangeMessageVisibilityBatchRequestEntry,
): CompletableFuture<ChangeMessageVisibilityBatchResponse> {
    return changeMessageVisibilityBatch {
        it.queueUrl(queueUrl)
        it.entries(*entries)
    }
}

fun SqsAsyncClient.changeMessageVisibilityBatch(
    queueUrl: String,
    entries: Collection<ChangeMessageVisibilityBatchRequestEntry>,
): CompletableFuture<ChangeMessageVisibilityBatchResponse> {
    return changeMessageVisibilityBatch {
        it.queueUrl(queueUrl)
        it.entries(entries)
    }
}

fun SqsAsyncClient.deleteMessage(
    queueUrl: String,
    receiptHandle: String? = null,
): CompletableFuture<DeleteMessageResponse> {
    return deleteMessage {
        it.queueUrl(queueUrl)
        it.receiptHandle(receiptHandle)
    }
}

fun SqsAsyncClient.deleteMessageBatch(
    queueUrl: String,
    vararg entries: DeleteMessageBatchRequestEntry,
): CompletableFuture<DeleteMessageBatchResponse> {
    return deleteMessageBatch {
        it.queueUrl(queueUrl)
        it.entries(*entries)
    }
}

fun SqsAsyncClient.deleteMessageBatch(
    queueUrl: String,
    entries: Collection<DeleteMessageBatchRequestEntry>,
): CompletableFuture<DeleteMessageBatchResponse> {
    return deleteMessageBatch {
        it.queueUrl(queueUrl)
        it.entries(entries)
    }
}

fun SqsAsyncClient.deleteQueue(queueUrl: String): CompletableFuture<DeleteQueueResponse> {
    return deleteQueue {
        it.queueUrl(queueUrl)
    }
}
