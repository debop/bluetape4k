package io.bluetape4k.kafka.spring

import kotlinx.coroutines.future.await
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.support.SendResult
import org.springframework.messaging.Message

suspend fun <K: Any, V: Any> KafkaOperations<K, V>.sendSuspending(record: ProducerRecord<K, V>): SendResult<K, V> =
    send(record).await()

suspend fun <K: Any, V: Any> KafkaOperations<K, V>.sendSuspending(message: Message<*>): SendResult<K, V> =
    send(message).await()

suspend fun <K: Any, V: Any> KafkaOperations<K, V>.sendSuspending(topic: String, value: V): SendResult<K, V> =
    send(topic, value).await()

suspend fun <K: Any, V: Any> KafkaOperations<K, V>.sendSuspending(topic: String, key: K, value: V): SendResult<K, V> =
    send(topic, key, value).await()

suspend fun <K: Any, V: Any> KafkaOperations<K, V>.sendSuspending(
    topic: String,
    partition: Int,
    key: K,
    value: V,
): SendResult<K, V> =
    send(topic, partition, key, value).await()

suspend fun <K: Any, V: Any> KafkaOperations<K, V>.sendSuspending(
    topic: String,
    partition: Int,
    timestamp: Long,
    key: K,
    value: V,
): SendResult<K, V> =
    send(topic, partition, timestamp, key, value).await()


suspend fun <K: Any, V: Any> KafkaOperations<K, V>.sendDefaultSuspending(value: V): SendResult<K, V> =
    sendDefault(value).await()

suspend fun <K: Any, V: Any> KafkaOperations<K, V>.sendDefaultSuspending(key: K, value: V): SendResult<K, V> =
    sendDefault(key, value).await()
