package io.bluetape4k.infra.kafka.spring.support

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.support.KafkaUtils

fun ProducerRecord<*, *>.prettyString(): String = KafkaUtils.format(this)
fun ConsumerRecord<*, *>.prettyString(): String = KafkaUtils.format(this)
