package io.bluetape4k.logback.kafka.export

import org.apache.kafka.clients.producer.BufferExhaustedException
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.errors.TimeoutException

class DefaultLogExporter: LogExporter {

    override fun <K, V, E> export(
        producer: Producer<K, V>,
        record: ProducerRecord<K, V>,
        event: E,
        fallback: (event: E, throwable: Throwable?) -> Unit,
    ): Boolean {
        return try {
            producer.send(record) { _, exception ->
                if (exception != null) {
                    fallback(event, exception)
                }
            }
            return true
        } catch (e: Throwable) {
            if (e is BufferExhaustedException || e is TimeoutException) {
                fallback(event, e)
            }
            false
        }
    }
}
