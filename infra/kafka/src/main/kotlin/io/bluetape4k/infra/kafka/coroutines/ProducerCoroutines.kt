package io.bluetape4k.infra.kafka.coroutines

import io.bluetape4k.kotlinx.coroutines.support.awaitSuspending
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

/**
 * Coroutine 환경 하에서 Producer를 이용하여 메시지를 producing 합니다.
 *
 * @param record record to produce
 * @return [RecordMetadata] instance
 */
suspend fun <K, V> Producer<K, V>.sendAwait(record: ProducerRecord<K, V>): RecordMetadata {
    return send(record).awaitSuspending()
}

/**
 * 복수의 [ProducerRecord] 를 producing 하면서, 결과들을 Flow로 반환하도록 합니다.
 *
 * @param records producing 할 record의 flow
 * @return producing 된 결과 ([RecordMetadata])의 flow
 */
fun <K, V> Producer<K, V>.sendFlow(records: Flow<ProducerRecord<K, V>>): Flow<RecordMetadata> {
    return records
        .map { record -> sendAwait(record) }
        .onCompletion { flush() }
}

/**
 * 복수의 [ProducerRecord] 를 producing 하면서, 마지막 producing 한 결과만 반환하게 한다.
 *
 * @param records producing 할 record의 flow
 * @return 마지막 record에 대한 producing 한 결과
 */
suspend fun <K, V> Producer<K, V>.sendFlowParallel(records: Flow<ProducerRecord<K, V>>): RecordMetadata {
    return coroutineScope {
        records
            .flatMapMerge { record ->
                flowOf(sendAwait(record))
            }
            .onCompletion { flush() }
            .last()
    }
}

/**
 * 발송만 하고, 결과 값은 받지 않습니다.
 *
 * @param records producing 할 record의 flow
 */
suspend fun <K, V> Producer<K, V>.sendAndForget(
    records: Flow<ProducerRecord<K, V>>,
    needFlush: Boolean = false
) {
    records
        .map { record -> send(record) }
        .collectLatest {
            if (needFlush) {
                flush()
            }
        }
}
