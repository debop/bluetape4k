package io.bluetape4k.json.gson

import io.bluetape4k.json.AbstractJsonSerializerTest
import io.bluetape4k.json.JsonSerializer
import io.bluetape4k.logging.KLogging

class GsonSerializerTest: AbstractJsonSerializerTest() {

    companion object: KLogging()

    override val serializer: JsonSerializer = GsonSerializer()

}
