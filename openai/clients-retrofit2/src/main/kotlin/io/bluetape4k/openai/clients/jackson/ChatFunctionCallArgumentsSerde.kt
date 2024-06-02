package io.bluetape4k.openai.clients.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.TextNode
import io.bluetape4k.json.jackson.Jackson
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error

class ChatFunctionCallArgumentsSerde {

    companion object: KLogging() {
        private val mapper = Jackson.defaultJsonMapper
    }

    class Serializer: JsonSerializer<JsonNode>() {
        override fun serialize(value: JsonNode?, gen: JsonGenerator, serializers: SerializerProvider) {
            if (value == null) {
                gen.writeNull()
            } else {
                val text = when (value) {
                    is TextNode -> value.asText()
                    else        -> value.toPrettyString()
                }
                gen.writeString(text)
            }
        }
    }

    class Deserializer: JsonDeserializer<JsonNode>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JsonNode? {
            val json = p.valueAsString
            if (json == null || p.currentToken == JsonToken.VALUE_NULL) {
                return null
            }

            return try {
                var node: JsonNode? = runCatching { mapper.readTree(json) }.getOrNull()
                if (node == null || node.nodeType == JsonNodeType.MISSING) {
                    node = mapper.readTree(p)
                }
                node
            } catch (e: Exception) {
                log.error(e) { "Fail to deserialize. json=$json" }
                null
            }
        }
    }
}
