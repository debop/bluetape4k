package io.bluetape4k.logback.kafka.exporter

import org.apache.kafka.clients.producer.BufferExhaustedException
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.errors.TimeoutException

class DefaultLogExporter: LogExporter {

    override fun <K, V, E> export(
        producer: Producer<K, V>,
        record: ProducerRecord<K, V>,
        event: E,
        fallback: ExportFallback<E>,
    ): Boolean {
        return try {
            producer.send(record) { recordMetadata, exception ->
                if (exception != null) {
                    fallback.handleException(event, exception)
                }
                println("export message. record=$record, recordMetadata=$recordMetadata, exception=$exception")
            }
            return true
        } catch (e: Throwable) {
            if (e is BufferExhaustedException || e is TimeoutException) {
                fallback.handleException(event, e)
            }
            false
        }
    }
}
