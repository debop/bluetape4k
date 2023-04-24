package io.bluetape4k.io.feign.codec

import com.fasterxml.jackson.databind.RuntimeJsonMappingException
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import feign.Response
import feign.Util
import feign.codec.DecodeException
import feign.codec.Decoder
import io.bluetape4k.io.feign.bodyAsReader
import io.bluetape4k.io.feign.isJsonBody
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.support.closeSafe
import java.io.BufferedReader
import java.io.IOException
import java.io.Reader
import java.lang.reflect.Type

/**
 * `Content-Type` 에 따라 `application/json` 이 아닌 경우에는 `text/plain` 방식으로 decode 해주는 Decoder 입니다.
 */
class JacksonDecoder2 private constructor(
    private val mapper: JsonMapper,
): Decoder {

    companion object: KLogging() {
        private val fallbackDecoder by lazy { Decoder.Default() }

        val INSTANCE: JacksonDecoder2 by lazy { invoke() }

        @JvmStatic
        operator fun invoke(mapper: JsonMapper = Jackson.defaultJsonMapper): JacksonDecoder2 {
            return JacksonDecoder2(mapper)
        }
    }

    inline fun <reified T> decode(response: Response): T? {
        return decode(response, jacksonTypeRef<T>().type) as? T
    }

    override fun decode(response: Response, type: Type): Any? = when {
        response.isJsonBody() -> runCatching { jsonDecode(response, type) }.getOrElse { fallback(response, type) }
        else -> fallback(response, type)
    }

    private fun jsonDecode(response: Response, type: Type): Any? {
        if (response.status() in listOf(204, 404)) {
            return Util.emptyValueOf(type)
        }
        if (response.body() == null) {
            return null
        }
        var reader: Reader = response.bodyAsReader()
        if (!reader.markSupported()) {
            reader = BufferedReader(reader, 1)
        }
        try {
            reader.mark(1)
            if (reader.read() == -1) {
                return null
            }
            reader.reset()
            log.debug { "Read json format response body. type=$type" }
            return mapper.readValue(reader, mapper.constructType(type))
        } catch (e: RuntimeJsonMappingException) {
            log.error(e) { "Failed to read json format response body. type=$type" }

            if (e.cause is IOException) {
                throw e.cause as IOException
            }
            throw DecodeException(
                response.status(),
                "$type is not a type supported by JacksonDecoder2 decoder.",
                response.request()
            )
        } finally {
            reader.closeSafe()
        }
    }

    private fun fallback(response: Response, type: Type): Any? {
        log.debug { "Read non-json format response body by fallback decoder. type=$type" }
        return fallbackDecoder.decode(response, type)
    }
}
