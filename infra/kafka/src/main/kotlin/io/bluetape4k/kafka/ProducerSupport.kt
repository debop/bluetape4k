package io.bluetape4k.kafka

import io.bluetape4k.support.asDoubleOrNull
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.Serializer
import java.util.*

fun <K, V> producerOf(
    configs: Map<String, Any?>,
    keySerializer: Serializer<K>? = null,
    valueSerializer: Serializer<V>? = null,
): Producer<K, V> {
    return KafkaProducer(configs, keySerializer, valueSerializer)
}

fun <K, V> producerOf(
    props: Properties,
    keySerializer: Serializer<K>? = null,
    valueSerializer: Serializer<V>? = null,
): Producer<K, V> {
    return KafkaProducer(props, keySerializer, valueSerializer)
}

/**
 * Producer 의 metrics 측정 값을 조회합니다.
 *
 * ```
 * val sendCount = producer.getMetricValue("record-send-total")
 * ```
 * @param metricName metric name to revrieve
 * @return metric 값
 */
fun <K, V> Producer<K, V>.getMetricValue(metricName: String): Double =
    getMetricValueOrNull(metricName)?.asDoubleOrNull() ?: 0.0

/**
 * Producer 의 metrics 측정 값을 조회합니다.
 *
 * ```
 * val sendCount = producer.getMetricValue("record-send-total")
 * ```
 * @param metricName metric name to revrieve
 * @return metric 값
 */
fun <K, V> Producer<K, V>.getMetricValueOrNull(metricName: String): Any? =
    metrics().entries.find { it.key.name() == metricName }?.value?.metricValue()
