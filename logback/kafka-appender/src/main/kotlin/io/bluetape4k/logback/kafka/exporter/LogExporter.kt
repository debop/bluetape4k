package io.bluetape4k.logback.kafka.exporter

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

/**
 * Log 정보를 Kafka 로 정송하는 Exporter의 기본 인터페이스
 *
 * @constructor Create empty Log exporter
 */
interface LogExporter {

    /**
     * Log 정보를 담은 [record]를 [producer]를 통해 Kafka 로 전송한다.
     *
     * @param K
     * @param V
     * @param E
     * @param producer
     * @param record
     * @param event
     * @param fallback
     * @receiver
     */
    fun <K, V, E> export(
        producer: Producer<K, V>,
        record: ProducerRecord<K, V>,
        event: E,
        fallback: ExportFallback<E>,
    ): Boolean

}
