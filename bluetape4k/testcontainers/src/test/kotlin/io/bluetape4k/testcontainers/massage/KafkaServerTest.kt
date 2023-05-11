package io.bluetape4k.testcontainers.massage

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.launch
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Execution(ExecutionMode.SAME_THREAD)
class KafkaServerTest {

    companion object: KLogging() {
        private const val TOPIC_NAME = "bluetape4k-test-topic-1"
        private const val TOPIC_NAME_CORUTINE = "bluetape4k-test-topic-coroutines-1"
    }

    @Test
    fun `launch kafka server`() {
        val kafka = KafkaServer.Launcher.kafka

        log.debug { "bootstrapServers=${kafka.bootstrapServers}" }
        log.debug { "boundPortNumbers=${kafka.boundPortNumbers}" }

        kafka.bootstrapServers.shouldNotBeNull()
        kafka.isRunning.shouldBeTrue()
    }

    @Test
    fun `producing and consuming messages`() {

        val producer = KafkaServer.Launcher.createStringProducer()

        val produced = AtomicBoolean(false)
        val record = ProducerRecord(TOPIC_NAME, "message-key", "Hello world")
        producer.send(record) { metadata, exception ->
            exception.shouldBeNull()
            metadata.topic() shouldBeEqualTo TOPIC_NAME
            metadata.partition() shouldBeGreaterOrEqualTo 0
            produced.set(true)
        }
        producer.flush()
        await.untilTrue(produced)

        val consumer = KafkaServer.Launcher.createStringConsumer()
        consumer.subscribe(listOf(TOPIC_NAME))
        var consumerRecords: ConsumerRecords<String, String>
        do {
            consumerRecords = consumer.poll(Duration.ofMillis(1000))
            if (!consumerRecords.isEmpty) {
                log.debug { "consumerRecords=$consumerRecords" }
            }
        } while (consumerRecords.isEmpty)

        consumerRecords.shouldNotBeNull()
        consumerRecords.count() shouldBeGreaterThan 0

        val consumerRecord = consumerRecords.first()
        consumerRecord.topic() shouldBeEqualTo TOPIC_NAME
        consumerRecord.key() shouldBeEqualTo "message-key"
        consumerRecord.value() shouldBeEqualTo "Hello world"

        consumer.commitSync()

        producer.close()
        closeConsumer(consumer)
    }

    @Test
    fun `producing with coroutines`() = runSuspendWithIO {
        val producer = KafkaServer.Launcher.createStringProducer()

        val producingJob = launch {
            val record = ProducerRecord(
                TOPIC_NAME_CORUTINE,
                "coroutine-key",
                "message in coroutines"
            )
            val metadata = producer.sendSuspending(record)

            metadata.topic() shouldBeEqualTo TOPIC_NAME_CORUTINE
            metadata.partition() shouldBeGreaterOrEqualTo 0
        }

        val consumer = KafkaServer.Launcher.createStringConsumer()
        consumer.subscribe(listOf(TOPIC_NAME_CORUTINE))

        producingJob.join()

        var consumerRecords: ConsumerRecords<String, String>
        do {
            consumerRecords = consumer.poll(Duration.ofMillis(1000))
            if (!consumerRecords.isEmpty) {
                log.debug { "consumerRecords=$consumerRecords" }
            }
        } while (consumerRecords.isEmpty)

        consumerRecords.shouldNotBeNull()
        consumerRecords.count() shouldBeGreaterThan 0

        val consumerRecord = consumerRecords.first()
        consumerRecord.topic() shouldBeEqualTo TOPIC_NAME_CORUTINE
        consumerRecord.key() shouldBeEqualTo "coroutine-key"
        consumerRecord.value() shouldBeEqualTo "message in coroutines"

        consumer.commitSync()

        producer.close()
        closeConsumer(consumer)
    }

    private suspend fun <K, V> Producer<K, V>.sendSuspending(
        record: ProducerRecord<K, V>,
    ): RecordMetadata = suspendCoroutine { cont ->
        send(record) { metadata, exception ->
            if (exception != null) {
                cont.resumeWithException(exception)
            } else {
                cont.resume(metadata)
            }
        }
    }

    private fun closeConsumer(consumer: Consumer<*, *>) {
        runCatching {
            consumer.unsubscribe()
            consumer.wakeup()
            consumer.close(Duration.ofSeconds(3))
        }
    }
}
