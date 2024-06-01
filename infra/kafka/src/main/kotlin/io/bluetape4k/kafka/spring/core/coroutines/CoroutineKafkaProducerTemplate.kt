package io.bluetape4k.kafka.spring.core.coroutines

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.Metric
import org.apache.kafka.common.MetricName
import org.apache.kafka.common.PartitionInfo
import org.springframework.beans.factory.DisposableBean
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.kafka.support.converter.MessagingMessageConverter
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.messaging.Message
import reactor.core.publisher.Flux
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import reactor.kafka.sender.SenderResult
import reactor.kafka.sender.TransactionManager
import kotlin.coroutines.CoroutineContext

/**
 * Coroutine kafka producer operations implementation.
 *
 * @see [org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate]
 */
class CoroutineKafkaProducerTemplate<K, V>(
    private val sender: KafkaSender<K, V>,
    private val messageConverter: RecordMessageConverter,
): CoroutineScope, AutoCloseable, DisposableBean {

    companion object: KLogging() {

        @JvmStatic
        @JvmOverloads
        operator fun <K, V> invoke(
            senderOptions: SenderOptions<K, V>,
            messageConverter: RecordMessageConverter = MessagingMessageConverter(),
        ): CoroutineKafkaProducerTemplate<K, V> {
            return CoroutineKafkaProducerTemplate(KafkaSender.create(senderOptions), messageConverter)
        }
    }

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job


    fun <T> sendTransactionally(records: Flow<SenderRecord<K, V, T>>): Flow<SenderResult<T>> {
        val result = sender.sendTransactionally(Flux.just(records.asFlux()))
        return result.flatMap { it }.asFlow()
    }

    suspend fun <T> sendTransactionally(record: SenderRecord<K, V, T>): SenderResult<T> {
        return sendTransactionally(flowOf(record)).first()
    }

    suspend fun send(topic: String, value: V): SenderResult<Unit> {
        return send(ProducerRecord(topic, value))
    }

    suspend fun send(topic: String, key: K, value: V): SenderResult<Unit> {
        return send(ProducerRecord(topic, key, value))
    }

    suspend fun send(topic: String, partition: Int, key: K, value: V): SenderResult<Unit> {
        return send(ProducerRecord(topic, partition, key, value))
    }

    suspend fun send(topic: String, message: Message<*>): SenderResult<Unit> {
        @Suppress("UNCHECKED_CAST")
        val producerRecord = messageConverter.fromMessage(message, topic) as ProducerRecord<K, V>
        if (!producerRecord.headers().iterator().hasNext()) {
            val correlationId = message.headers[KafkaHeaders.CORRELATION_ID, ByteArray::class.java]
            if (correlationId != null) {
                producerRecord.headers().add(KafkaHeaders.CORRELATION_ID, correlationId)
            }
        }
        return send(producerRecord)
    }

    suspend fun send(record: ProducerRecord<K, V>): SenderResult<Unit> {
        return send(SenderRecord.create(record, Unit))
    }

    suspend fun <T> send(record: SenderRecord<K, V, T>): SenderResult<T> {
        return send(flowOf(record)).first()
    }

    fun <T> send(records: Flow<SenderRecord<K, V, T>>): Flow<SenderResult<T>> {
        return sender.send(records.asFlux()).asFlow()
    }

    suspend fun partitionsFromProducerFor(topic: String): List<PartitionInfo> {
        return doOnProducer { it.partitionsFor(topic) }
    }

    suspend fun metricsFromProducer(): Map<MetricName, Metric> {
        return doOnProducer { producer -> producer.metrics() }
    }

//    /**
//     * Flush the producer.
//     * @return {@link Mono#empty()}.
//     * @deprecated - flush does not make sense in the context of a reactive flow since,
//     * the send completions signal is a send result, which implies that a flush is
//     * redundant. If you use this method with reactor-kafka 1.3 or later, it must be
//     * scheduled to avoid a deadlock; see
//     * https://issues.apache.org/jira/browse/KAFKA-10790 (since 2.7).
//     */
//    @Deprecated("")
//    suspend fun flush() {
//        doOnProducer { producer ->
//            producer.flush()
//        }
//    }

    suspend fun <T> doOnProducer(action: (producer: Producer<K, V>) -> T): T {
        return sender.doOnProducer { action(it) }.awaitSingle()
    }

    val transactionManager: TransactionManager
        get() = sender.transactionManager()

    override fun close() {
        doClose()
    }

    override fun destroy() {
        doClose()
    }

    private fun doClose() {
        sender.close()
    }
}
