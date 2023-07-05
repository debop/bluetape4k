package io.bluetape4k.openai.clients.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.models.chat.ChatFunctionMode

class ChatFunctionModeSerde {

    @BetaOpenAI
    class Serializer: JsonSerializer<ChatFunctionMode>() {
        override fun serialize(value: ChatFunctionMode?, gen: JsonGenerator, serializers: SerializerProvider) {
            if (value?.name == null) {
                gen.writeNull()
            } else if (value == ChatFunctionMode.None || value == ChatFunctionMode.Auto) {
                gen.writeString(value.name)
            } else {
                gen.writeStartObject()
                gen.writeFieldName("name")
                gen.writeString(value.name)
                gen.writeEndObject()
            }
        }
    }

    @BetaOpenAI
    class Deserializer: JsonDeserializer<ChatFunctionMode>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ChatFunctionMode? {
            if (p.currentToken.isStructStart) {
                p.nextToken()
                p.nextToken()
            }
            return ChatFunctionMode(p.valueAsString)
        }
    }
}
