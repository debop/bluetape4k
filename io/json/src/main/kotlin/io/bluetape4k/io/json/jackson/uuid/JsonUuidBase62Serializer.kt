package io.bluetape4k.io.json.jackson.uuid

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer
import io.bluetape4k.codec.Url62
import java.util.UUID

class JsonUuidBase62Serializer: UUIDSerializer() {

    override fun serialize(value: UUID?, gen: JsonGenerator, provider: SerializerProvider?) {
        value?.run { gen.writeString(Url62.encode(this)) }
    }
}
