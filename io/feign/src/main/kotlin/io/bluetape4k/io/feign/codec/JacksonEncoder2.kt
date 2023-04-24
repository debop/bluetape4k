package io.bluetape4k.io.feign.codec

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.json.JsonMapper
import feign.RequestTemplate
import feign.codec.EncodeException
import feign.codec.Encoder
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.logging.KLogging
import java.lang.reflect.Type

/**
 * `jackson-modules-kotlin` 을 사용하여, Kotlin 수형에 대해서도 처리가 가능한 JSON 용 Encoder 입니다.
 */
class JacksonEncoder2 private constructor(
    private val mapper: JsonMapper,
) : Encoder {

    companion object : KLogging() {

        val INSTANCE: JacksonEncoder2 by lazy { invoke() }

        @JvmStatic
        operator fun invoke(mapper: JsonMapper = Jackson.defaultJsonMapper): JacksonEncoder2 {
            return JacksonEncoder2(mapper)
        }
    }

    override fun encode(obj: Any?, bodyType: Type, template: RequestTemplate) {
        try {
            val javaType = mapper.typeFactory.constructType(bodyType)
            template.body(mapper.writerFor(javaType).writeValueAsBytes(obj), Charsets.UTF_8)
        } catch (e: JsonProcessingException) {
            throw EncodeException(e.message, e)
        }
    }
}
