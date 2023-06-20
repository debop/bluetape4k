package io.bluetape4k.spring.kafka

import io.bluetape4k.spring.coroutines.await
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult

suspend fun <K: Any, V: Any> KafkaTemplate<K, V>.sendSuspending(record: ProducerRecord<K, V>): SendResult<K, V> =
    send(record).await()


suspend fun <K: Any, V: Any> KafkaTemplate<K, V>.sendDefaultSuspending(value: V?): SendResult<K, V> =
    sendDefault(value).await()

suspend fun <K: Any, V: Any> KafkaTemplate<K, V>.sendDefaultSuspending(key: K, value: V?): SendResult<K, V> =
    sendDefault(key, value).await()
