package io.bluetape4k.jackson.binary

import com.fasterxml.jackson.dataformat.ion.IonObjectMapper
import io.bluetape4k.json.jackson.JacksonSerializer
import io.bluetape4k.logging.KLogging

class IonJsonSerializer(
    mapper: IonObjectMapper = JacksonBinary.ION.defaultMapper,
): JacksonSerializer(mapper) {

    companion object: KLogging()

}
