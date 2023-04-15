package io.bluetape4k.io.json.jackson.uuid

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer
import io.bluetape4k.codec.Url62
import io.bluetape4k.logging.KLogging
import java.util.UUID

class JsonUuidBase62Deserializer: UUIDDeserializer() {

    companion object: KLogging() {
        private val UUID_PATTERN =
            "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}".toRegex()
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): UUID {
        val token = p.currentToken
        if (token == JsonToken.VALUE_STRING) {
            val text = p.valueAsString.trim()
            return if (looksLikeUuid(text)) {
                super.deserialize(p, ctxt)
            } else {
                Url62.decode(text)
            }
        }
        error("This is not uuid or url62 encoded id. name=${p.currentName}, value=${p.currentValue}")
    }

    private fun looksLikeUuid(value: String): Boolean = UUID_PATTERN.matches(value)
}
