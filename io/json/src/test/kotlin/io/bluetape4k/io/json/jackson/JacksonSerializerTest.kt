package io.bluetape4k.io.json.jackson

import io.bluetape4k.io.json.AbstractJsonSerializerTest
import io.bluetape4k.io.json.JsonSerializer
import io.bluetape4k.logging.KLogging

class JacksonSerializerTest: AbstractJsonSerializerTest() {

    companion object: KLogging()

    override val serializer: JsonSerializer = JacksonSerializer()

}
