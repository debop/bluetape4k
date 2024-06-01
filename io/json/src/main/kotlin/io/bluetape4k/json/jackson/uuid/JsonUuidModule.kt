package io.bluetape4k.json.jackson.uuid

import com.fasterxml.jackson.databind.module.SimpleModule
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import java.util.*

/**
 * UUID 수형을 Base62 로 인코딩/디코딩 하는 Module 입니다.
 */
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
