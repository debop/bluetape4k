package io.bluetape4k.infra.kafka

import io.bluetape4k.core.assertNotBlank
import org.apache.kafka.common.TopicPartition

fun String.toTopicPartition(): TopicPartition = topicPartitionOf(this)

fun topicPartitionOf(tp: String): TopicPartition {
    tp.assertNotBlank("tp")
    require(tp.contains("-")) { "Not found kafka topic-position delimiter (-)" }

    val (topic, partition) = tp.split("-", limit = 2)
    return TopicPartition(topic, partition.toInt())
}
