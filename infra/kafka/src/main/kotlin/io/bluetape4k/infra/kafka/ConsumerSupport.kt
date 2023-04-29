package io.bluetape4k.infra.kafka

import java.util.Properties
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Deserializer

fun <K, V> consumerOf(
    configs: Map<String, Any?>,
    keyDeserializer: Deserializer<K>? = null,
    valueDeserializer: Deserializer<V>? = null
): Consumer<K, V> {
    return KafkaConsumer(configs, keyDeserializer, valueDeserializer)
}

fun <K, V> consumerOf(
    props: Properties,
    keyDeserializer: Deserializer<K>? = null,
    valueDeserializer: Deserializer<V>? = null
): Consumer<K, V> {
    return KafkaConsumer(props, keyDeserializer, valueDeserializer)
}
