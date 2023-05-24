package io.bluetape4k.infra.kafka.codec

import com.fasterxml.jackson.databind.json.JsonMapper
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.io.json.jackson.writeAsBytes
import io.bluetape4k.support.emptyByteArray
import org.apache.kafka.common.header.Headers

class JacksonKafkaCodec<T: Any>(
    private val mapper: JsonMapper = Jackson.defaultJsonMapper,
): AbstractKafkaCodec<T>() {

    override fun doSerialize(topic: String?, headers: Headers?, graph: T): ByteArray {
        return mapper.writeAsBytes(graph) ?: emptyByteArray
    }

    @Suppress("UNCHECKED_CAST")
    override fun doDeserialize(topic: String?, headers: Headers?, bytes: ByteArray): T? {
        val clazz = getValueType(headers)
        return if (bytes.isEmpty()) null
        else mapper.readValue(bytes, clazz) as? T
    }
}
