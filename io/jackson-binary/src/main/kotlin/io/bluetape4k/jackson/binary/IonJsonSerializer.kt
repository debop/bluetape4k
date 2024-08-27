package io.bluetape4k.jackson.binary

import com.fasterxml.jackson.dataformat.ion.IonObjectMapper
import io.bluetape4k.json.jackson.JacksonSerializer
import io.bluetape4k.logging.KLogging

/**
 * Binary JSON 직렬화를 위한 Ion Serializer
 *
 * @param mapper Jackson [IonObjectMapper] 인스턴스
 */
class IonJsonSerializer(
    mapper: IonObjectMapper = JacksonBinary.ION.defaultMapper,
): JacksonSerializer(mapper) {

    companion object: KLogging()

}
