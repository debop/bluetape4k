package io.bluetape4k.infra.kafka.coroutines

import io.bluetape4k.concurrent.asCompletableFuture
import io.bluetape4k.concurrent.onComplete
import io.bluetape4k.concurrent.sequence
import io.bluetape4k.infra.kafka.AbstractKafkaTest
import io.bluetape4k.infra.kafka.getMetricValue
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.kotlinx.coroutines.support.awaitSuspending
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.testcontainers.massage.KafkaServer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.junit.jupiter.api.RepeatedTest
import java.util.concurrent.CompletableFuture
import kotlin.system.measureTimeMillis

@RandomizedTest
class ProducerSupportTest: AbstractKafkaTest() {

    companion object: KLogging() {
        private const val MESSAGE_SIZE = 100

        fun randomStrings(size: Int = MESSAGE_SIZE): List<String> {
            return List(size) { randomString() }
        }
    }

    private val producer = KafkaServer.Launcher.createStringProducer()

    @RepeatedTest(REPEAT_SIZE)
    fun `send one message with future`(@RandomValue message: String) = runSuspendWithIO {
        val record = ProducerRecord<String?, String>(TEST_TOPIC_NAME, null, message)

        val future: CompletableFuture<RecordMetadata> = producer.send(record).asCompletableFuture()
        producer.flush()
        val metadata = future.awaitSuspending()
        metadata.verifyRecordMetadata()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `send one message in suspend`(@RandomValue message: String) = runSuspendWithIO {
        val record = ProducerRecord<String, String>(TEST_TOPIC_NAME, null, message)

        val metadata = producer.sendAwait(record)
        metadata.verifyRecordMetadata()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `send many messages with future`() {
        val messages = randomStrings()

        measureSendRecords(MESSAGE_SIZE) {
            val futures = messages.map { message ->
                val record = ProducerRecord<String, String>(TEST_TOPIC_NAME, null, message)

                producer.send(record).asCompletableFuture()
                    .onComplete { metadata, _ ->
                        metadata!!.verifyRecordMetadata()
                    }
            }
            futures.sequence().join()
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `send many messages with suspend`() {
        val messages = randomStrings()

        measureSendRecords(MESSAGE_SIZE) {
            val defers = messages.map { message ->
                val record = ProducerRecord<String, String>(TEST_TOPIC_NAME, null, message)
                async(Dispatchers.IO) { producer.sendAwait(record) }
            }

            defers.awaitAll().forEach { metadata ->
                metadata.verifyRecordMetadata()
            }
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `send flow messages all async`() {
        val messages = randomStrings()

        measureSendRecords(MESSAGE_SIZE) {
            val sendTime = measureTimeMillis {
                val records = messages.asFlow()
                    .map { ProducerRecord<String, String>(TEST_TOPIC_NAME, null, it) }

                val lastResult = producer.sendFlow(records).last()
                lastResult.verifyRecordMetadata()
            }
            log.debug { "Send time=$sendTime" }
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `send flow messages in bulk mode`() {
        val messages = randomStrings()

        measureSendRecords(MESSAGE_SIZE) {
            val sendTime = measureTimeMillis {
                val records = messages.asFlow()
                    .map { ProducerRecord<String, String>(TEST_TOPIC_NAME, null, it) }

                val lastResult = producer.sendFlowParallel(records)
                lastResult.verifyRecordMetadata()
            }
            log.debug { "Send time=$sendTime" }
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `send and forget flow messages`() {
        val messages = randomStrings()

        runBlocking(Dispatchers.IO) {
            val prevSentTotal = producer.getMetricValue("record-send-total")

            val sendTime = measureTimeMillis {
                val records = messages.asFlow()
                    .map { ProducerRecord<String, String>(TEST_TOPIC_NAME, null, it) }

                producer.sendAndForget(records, true)
            }

            log.debug { "Send time=$sendTime" }
            val currSentTotal = producer.getMetricValue("record-send-total") - prevSentTotal
            log.debug { "Current sent count=$currSentTotal" }
        }
    }

    private fun measureSendRecords(
        expectCount: Int = MESSAGE_SIZE,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        runBlocking(Dispatchers.IO) {
            val prevSentTotal = producer.getMetricValue("record-send-total")

            block()

            val currSentTotal = producer.getMetricValue("record-send-total") - prevSentTotal
            log.debug { "Current sent count=$currSentTotal" }
            currSentTotal.toInt() shouldBeGreaterOrEqualTo expectCount
        }
    }

    private fun RecordMetadata.verifyRecordMetadata() {
        topic() shouldBeEqualTo TEST_TOPIC_NAME
        partition() shouldBeGreaterOrEqualTo 0
        // ACK >= 1 이어야만 유효합니다.
        // offset() shouldBeGreaterOrEqualTo 0
    }
}