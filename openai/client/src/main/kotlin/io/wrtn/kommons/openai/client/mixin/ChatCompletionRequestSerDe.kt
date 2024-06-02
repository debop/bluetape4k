package io.bluetape4k.openai.client.mixin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.bluetape4k.json.jackson.writeString
import io.bluetape4k.openai.client.model.core.FunctionCall

class ChatCompletionRequestSerDe {

    class Serializer: JsonSerializer<FunctionCall>() {
        override fun serialize(value: FunctionCall?, gen: JsonGenerator, serializers: SerializerProvider) {
            if (value?.name == null) {
                gen.writeNull()
            } else if (value.name == "none" || value.name == "auto") {
                gen.writeString(value.name)
            } else {
                gen.writeString("name", value.name)
            }
        }
    }

    class Deserializer: JsonDeserializer<FunctionCall>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): FunctionCall {
            if (p.currentToken.isStructStart) {
                p.nextToken()   // key
                p.nextToken()   // value
            }

            return FunctionCall(name = p.valueAsString)
        }
    }
}
