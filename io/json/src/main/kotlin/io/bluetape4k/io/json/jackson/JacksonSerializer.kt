package io.bluetape4k.io.json.jackson

import com.fasterxml.jackson.databind.json.JsonMapper
import io.bluetape4k.io.json.JsonSerializer
import io.bluetape4k.support.emptyByteArray

open class JacksonSerializer(
    val mapper: JsonMapper = Jackson.defaultJsonMapper,
): JsonSerializer {

    override fun serialize(graph: Any?): ByteArray {
        return graph?.run { mapper.writeValueAsBytes(this) } ?: emptyByteArray
    }

    override fun <T: Any> deserialize(bytes: ByteArray?, clazz: Class<T>): T? {
        return bytes?.run { mapper.readValue(this, clazz) }
    }
}
