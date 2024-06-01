package io.bluetape4k.jackson.binary

import com.fasterxml.jackson.dataformat.smile.databind.SmileMapper
import io.bluetape4k.json.jackson.JacksonSerializer
import io.bluetape4k.logging.KLogging

/**
 * Binary JSON 직렬화를 위한 Smile Serializer
 *
 * @param mapper Jackson [SmileMapper] 인스턴스
 */
class SmileJsonSerializer(
    mapper: SmileMapper = JacksonBinary.Smile.defaultMapper,
): JacksonSerializer(mapper) {

    companion object: KLogging()
}
