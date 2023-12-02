package io.bluetape4k.logback.kafka

import ch.qos.logback.core.UnsynchronizedAppenderBase
import ch.qos.logback.core.encoder.Encoder
import ch.qos.logback.core.spi.AppenderAttachable
import io.bluetape4k.logback.kafka.exporter.DefaultLogExporter
import io.bluetape4k.logback.kafka.exporter.LogExporter
import io.bluetape4k.logback.kafka.keyprovider.KeyProvider
import io.bluetape4k.logback.kafka.keyprovider.NullKeyProvider
import org.apache.kafka.clients.producer.ProducerConfig

abstract class KafkaAppenderOptions<E>: UnsynchronizedAppenderBase<E>(), AppenderAttachable<E> {

    var bootstrapServers: String? = null
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
    var keyProvider: KeyProvider<in E>? = null
    var logExporter: LogExporter? = null

    var producerConfig: MutableMap<String, Any?> = mutableMapOf()

    fun addProducerConfigValue(key: String, value: Any?) {
        producerConfig[key] = value
    }

    fun addProducerConfigValue(keyValue: String) {
        val (key, value) = keyValue.split("=", limit = 2)
        addProducerConfigValue(key, value)
    }


    fun checkOptions(): Boolean {
        var validOptions = true

        producerConfig[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers

        if (bootstrapServers.isNullOrBlank()) {
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

        if (keyProvider == null) {
            addInfo("keyProvider is not set. use NullKeyProvider")
            keyProvider = NullKeyProvider()
        }

        if (logExporter == null) {
            addInfo("logExporter is not set, use DefaultLogExporter")
            logExporter = DefaultLogExporter()
        }

        println("Check options. valid options=$validOptions")
        return validOptions
    }
}
