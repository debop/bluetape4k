package io.bluetape4k.jackson.binary.cbor

import io.bluetape4k.jackson.binary.AbstractJacksonBinaryTest
import io.bluetape4k.jackson.binary.JacksonBinary
import io.bluetape4k.json.JsonSerializer

class CborMapperTest: AbstractJacksonBinaryTest() {

    override val binaryJsonSerializer: JsonSerializer = JacksonBinary.CBOR.defaultJsonSerializer

}
