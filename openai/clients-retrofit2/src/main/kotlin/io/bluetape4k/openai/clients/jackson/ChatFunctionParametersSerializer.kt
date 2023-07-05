package io.bluetape4k.openai.clients.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator
import io.bluetape4k.io.json.jackson.Jackson

class ChatFunctionParametersSerializer: JsonSerializer<Class<*>>() {

    private val mapper = Jackson.defaultJsonMapper

    // https://github.com/FasterXML/jackson-module-jsonSchema
    private val jsonSchemaGenerator = JsonSchemaGenerator(mapper)

    override fun serialize(value: Class<*>?, gen: JsonGenerator, serializers: SerializerProvider) {
        if (value == null) {
            gen.writeNull()
        } else {
            try {
                val schema = jsonSchemaGenerator.generateSchema(value)
                gen.writeObject(schema)
            } catch (e: Throwable) {
                throw RuntimeException("Fail to generate JSON Schema. value=$value", e)
            }
        }
    }
}
