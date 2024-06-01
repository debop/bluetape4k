package io.bluetape4k.jackson.binary

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import io.bluetape4k.json.jackson.JacksonSerializer
import io.bluetape4k.logging.KLogging

/**
 * Binary JSON 직렬화를 위한 CBOR Serializer
 *
 * @param mapper Jackson [CBORMapper] 인스턴스
 */
class CborJsonSerializer(
    mapper: CBORMapper = JacksonBinary.CBOR.defaultMapper,
): JacksonSerializer(mapper) {

    companion object: KLogging()
}
