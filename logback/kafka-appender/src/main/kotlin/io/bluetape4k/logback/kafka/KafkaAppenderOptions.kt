package io.bluetape4k.logback.kafka

import ch.qos.logback.core.encoder.Encoder
import ch.qos.logback.core.spi.ContextAwareBase
import io.bluetape4k.logback.kafka.export.DefaultLogExporter
import io.bluetape4k.logback.kafka.export.LogExporter
import io.bluetape4k.logback.kafka.keycreator.KeyCreator
import io.bluetape4k.logback.kafka.keycreator.NullKeyCreator
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArraySerializer

internal class KafkaAppenderOptions<E>: ContextAwareBase() {

    var topic: String? = null
    var partition: Int? = null
        set(value) {
            field = if (value != null && value >= 0) {
                value
            } else {
                null
            }
        }
    var needAppendTimestamp: Boolean = true
    var encoder: Encoder<E>? = null
    var keyCreator: KeyCreator<E>? = null
    var logExporter: LogExporter? = null

    val producerConfig: MutableMap<String, Any?> = mutableMapOf(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java
    )

    fun addProducerConfigValue(key: String, value: Any?) {
        producerConfig[key] = value
    }

    fun checkOptions(): Boolean {
        var validOptions = true

        if (producerConfig[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] == null) {
            addError("bootstrap.servers is not set")
            validOptions = false
        }

        if (topic.isNullOrBlank()) {
            addError("topic is not set")
            validOptions = false
        }

        if (encoder == null) {
            addError("encoder is not set")
            validOptions = false
        }

        if (keyCreator == null) {
            addInfo("keyCreator is not set. use NullKeyCreator")
            keyCreator = NullKeyCreator()
        }

        if (logExporter == null) {
            addInfo("logExporter is not set, use DefaultLogExporter")
            logExporter = DefaultLogExporter()
        }

        return validOptions
    }
}
