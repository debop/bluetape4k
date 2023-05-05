package io.bluetape4k.aws.sqs

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import kotlinx.coroutines.future.await
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import software.amazon.awssdk.services.sqs.model.DeleteQueueResponse
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry
import java.util.*

@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SqsAsyncClientSupport: AbstractSqsTest() {

    companion object: KLogging() {
        private const val QUEUE_PREFIX = "async-queue"
        private val QUEUE_NAME = "$QUEUE_PREFIX-${UUID.randomUUID().encodeBase62().lowercase()}"
    }

    private lateinit var queueUrl: String

    @Test
    @Order(1)
    fun `create queue`() = runSuspendWithIO {
        val url = asyncClient.createQueue(QUEUE_NAME).await()
        queueUrl = asyncClient.getQueueUrl(QUEUE_NAME).await().queueUrl()

        queueUrl shouldBeEqualTo url
        log.debug { "queue url=$queueUrl" }
    }

    @Test
    @Order(2)
    fun `list queues`() = runSuspendWithIO {
        val response = asyncClient.listQueues(QUEUE_PREFIX).await()

        response.queueUrls() shouldHaveSize 1
        response.queueUrls().forEach {
            log.debug { "queue url=$it" }
        }
    }

    @Test
    @Order(3)
    fun `send message`() = runSuspendWithIO {
        val response = asyncClient.send(queueUrl, "Hello, World!").await()
        response.messageId().shouldNotBeEmpty()
        log.debug { "response=$response" }
    }

    @Test
    @Order(4)
    fun `send messages in batch mode`() = runSuspendWithIO {
        // NOTE: The total size of all messages that you send in a single SendMessageBatch call can't exceed 262,144 bytes (256 KB).
        // https://stackoverflow.com/questions/40489815/checking-size-of-sqs-message-batches
        // 이 것 계산하려면 Jdk Serializer를 통해서 bytes 를 계산해야 한다
        val entries = List(10) {
            SendMessageBatchRequestEntry.builder()
                .id("id-$it")
                .messageBody("Hello, world $it")
                .build()
        }
        val response = asyncClient.sendBatch(queueUrl, entries).await()
        response.successful() shouldHaveSize entries.size
        response.successful().forEach {
            log.debug { "result entry=$it" }
        }
    }

    @Test
    @Order(5)
    fun `receive messages`() = runSuspendWithIO {
        val messages = asyncClient.receiveMessages(queueUrl, 3).await().messages()

        messages shouldHaveSize 3
        messages.forEach {
            log.trace { "message=$it" }
        }
    }

    @Test
    @Order(6)
    fun `chnage messages`() = runSuspendWithIO {
        val messages = asyncClient.receiveMessages(queueUrl, 3).await().messages()

        val responses = messages.map { message ->
            asyncClient.changeMessageVisibility(queueUrl, message.receiptHandle(), 10).await()
            client.changeMessageVisibility {
                it.queueUrl(queueUrl).receiptHandle(message.receiptHandle()).visibilityTimeout(10)
            }
        }
        responses shouldHaveSize messages.size
        responses.forEach {
            log.debug { "response  metadata=${it.responseMetadata()}" }
        }
    }

    @Test
    @Order(7)
    fun `delete messages`() = runSuspendWithIO {
        val messages = asyncClient.receiveMessages(queueUrl, 3).await().messages()

        val responses = messages.map { message ->
            asyncClient.deleteMessage(queueUrl, message.receiptHandle()).await()
        }
        responses shouldHaveSize messages.size
        responses.forEach {
            log.debug { "response=$it" }
        }
    }

    @Test
    @Order(8)
    fun `delete queue`() = runSuspendWithIO {
        val response: DeleteQueueResponse = asyncClient.deleteQueue(queueUrl).await()
        response.responseMetadata().requestId().shouldNotBeEmpty()
    }
}
