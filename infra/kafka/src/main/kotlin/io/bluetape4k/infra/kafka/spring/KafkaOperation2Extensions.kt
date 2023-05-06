package io.bluetape4k.infra.kafka.spring

import io.bluetape4k.support.asDouble
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.suspendCancellableCoroutine
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.Metric
import org.springframework.kafka.core.KafkaOperations2
import org.springframework.kafka.support.SendResult
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


suspend fun <K, V> KafkaOperations2<K, V>.sendAwait(
    record: ProducerRecord<K, V>,
): SendResult<K, V> {
    return suspendCancellableCoroutine { cont ->
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
}

/**
 * 복수의 [ProducerRecord] 를 producing 하면서, 마지막 producing 한 결과만 반환하게 한다.
 *
 * @param records producing 할 record의 flow
 * @return 마지막 record에 대한 producing 한 결과
 */
suspend fun <K, V> KafkaOperations2<K, V>.sendFlowAsParallel(
    records: Flow<ProducerRecord<K, V>>,
): SendResult<K, V> = coroutineScope {
    records
        .flatMapMerge {
            flowOf(sendAwait(it))
        }
        .onCompletion { flush() }
        .last()
}

/**
 * 발송만 하고, 결과 값은 받지 않습니다.
 *
 * @param records producing 할 record의 flow
 */
suspend fun <K, V> KafkaOperations2<K, V>.sendAndForget(
    records: Flow<ProducerRecord<K, V>>,
    needFlush: Boolean = false,
) {
    records
        .flatMapMerge {
            flowOf(sendAwait(it))
        }
        .onCompletion {
            if (needFlush) flush()
        }
}

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
