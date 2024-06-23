package io.bluetape4k.json.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.bluetape4k.json.JsonSerializer
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.emptyByteArray

/**
 * Jackson JSON 직렬화/역직렬화를 위한 Serializer
 *
 * @param mapper Jackson [ObjectMapper] 인스턴스
 */
open class JacksonSerializer(
    val mapper: ObjectMapper = Jackson.defaultJsonMapper,
): JsonSerializer {

    companion object: KLogging()

    override fun serialize(graph: Any?): ByteArray {
        return graph?.run { mapper.writeAsBytes(this) } ?: emptyByteArray
    }

    override fun <T: Any> deserialize(bytes: ByteArray?, clazz: Class<T>): T? {
        return bytes?.run { mapper.readValue(this, clazz) }
    }
}
