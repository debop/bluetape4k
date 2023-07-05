package io.bluetape4k.infra.kafka.spring.core

import io.bluetape4k.coroutines.flow.async
import io.bluetape4k.support.asDouble
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.suspendCancellableCoroutine
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.Metric
import org.springframework.kafka.core.KafkaOperations2
import org.springframework.kafka.support.SendResult
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Coroutines 환경 하에서 Kafka [Producer]를 이용하여 메시지를 전송합니다
 *
 * @param K Key type
 * @param V Value type
 * @param record 전송할 정보 [ProducerRecord]
 * @return 발송 결과 정보 [SendResult]
 */
suspend inline fun <K, V> KafkaOperations2<K, V>.sendSuspending(
    record: ProducerRecord<K, V>,
): SendResult<K, V> = suspendCancellableCoroutine { cont ->
    val result = execute { producer ->
        producer.send(record) { metadata, exception ->
            if (exception != null) {
                cont.resumeWithException(exception)
            } else {
                cont.resume(SendResult(record, metadata))
            }
        }
    }
    cont.invokeOnCancellation { result?.cancel(true) }
}

/**
 * 복수의 [ProducerRecord] 를 producing 하면서, 마지막 producing 한 결과만 반환하게 한다.
 *
 * @param records producing 할 record의 flow
 * @return 마지막 record에 대한 producing 한 결과
 */
suspend inline fun <K, V> KafkaOperations2<K, V>.sendFlowAsParallel(
    records: Flow<ProducerRecord<K, V>>,
): SendResult<K, V> {
    return records
        .async {
            sendSuspending(it)
        }
        .onCompletion { flush() }
        .last()
}

/**
 * 발송만 하고, 결과 값은 받지 않습니다.
 *
 * @param records producing 할 record의 flow
 */
suspend inline fun <K, V> KafkaOperations2<K, V>.sendAndForget(
    records: Flow<ProducerRecord<K, V>>,
    needFlush: Boolean = false,
) {
    records
        .async {
            sendSuspending(it)
        }
        .onCompletion {
            if (needFlush) flush()
        }
        .collect()
}

/**
 * Producer 의 metrics 측정 값을 조회합니다.
 *
 * ```
 * val metric = producer.getMetric("record-send-total")
 * ```
 * @param metricName metric name to revrieve
 * @return [Metric] 인스턴스 또는 null
 */
fun <K, V> KafkaOperations2<K, V>.getMetric(metricName: String): Metric? =
    metrics().entries.find { it.key.name() == metricName }?.value


/**
 * Producer 의 metrics 측정 값을 조회합니다.
 *
 * ```
 * val sendCount = producer.getMetricValue("record-send-total")
 * ```
 * @param metricName metric name to revrieve
 * @return metric 값
 */
fun <K, V> KafkaOperations2<K, V>.getMetricValue(metricName: String): Double =
    getMetric(metricName)?.metricValue().asDouble(0.0)

/**
 * Producer 의 metrics 측정 값을 조회합니다.
 *
 * ```
 * val sendCount = producer.getMetricValue("record-send-total")
 * ```
 * @param metricName metric name to revrieve
 * @return metric 값
 */
fun <K, V> KafkaOperations2<K, V>.getMetricValueOrNull(metricName: String): Any? =
    getMetric(metricName)?.metricValue()
