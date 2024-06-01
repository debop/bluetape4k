package io.bluetape4k.kafka.codec

import com.fasterxml.jackson.databind.json.JsonMapper
import io.bluetape4k.json.jackson.Jackson
import io.bluetape4k.json.jackson.writeAsBytes
import io.bluetape4k.support.emptyByteArray
import org.apache.kafka.common.header.Headers

class JacksonKafkaCodec(
    private val mapper: JsonMapper = Jackson.defaultJsonMapper,
): AbstractKafkaCodec<Any?>() {

    override fun doSerialize(topic: String?, headers: Headers?, graph: Any?): ByteArray {
        return mapper.writeAsBytes(graph) ?: emptyByteArray
    }

    override fun doDeserialize(topic: String?, headers: Headers?, bytes: ByteArray): Any? {
        val clazz = getValueType(headers)
        return if (bytes.isEmpty()) null
        else mapper.readValue(bytes, clazz)
    }
}
