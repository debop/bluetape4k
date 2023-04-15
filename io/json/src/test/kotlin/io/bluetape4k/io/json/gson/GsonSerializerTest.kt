package io.bluetape4k.io.json.gson

import io.bluetape4k.io.json.AbstractJsonSerializerTest
import io.bluetape4k.io.json.JsonSerializer
import io.bluetape4k.logging.KLogging

class GsonSerializerTest: AbstractJsonSerializerTest() {

    companion object: KLogging()

    override val serializer: JsonSerializer = GsonSerializer()

}
