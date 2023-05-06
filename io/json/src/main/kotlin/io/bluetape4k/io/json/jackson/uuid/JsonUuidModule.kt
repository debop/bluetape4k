package io.bluetape4k.io.json.jackson.uuid

import com.fasterxml.jackson.databind.module.SimpleModule
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import java.util.*

class JsonUuidModule: SimpleModule() {

    companion object: KLogging()

    private val interospector = JsonUuidEncoderAnnotationInterospector()

    init {
        log.debug { "Add JsonUuidBase62Serializer ..." }
        addSerializer(UUID::class.java, JsonUuidBase62Serializer())

        log.debug { "Add JsonUuidBase62Deserializer ..." }
        addDeserializer(UUID::class.java, JsonUuidBase62Deserializer())
    }

    override fun setupModule(context: SetupContext) {
        log.info { "Setup JsonUuidModule ..." }
        context.insertAnnotationIntrospector(interospector)
    }
}
