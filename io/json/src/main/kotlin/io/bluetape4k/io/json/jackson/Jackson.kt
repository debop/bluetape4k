package io.bluetape4k.io.json.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder
import io.bluetape4k.io.json.jackson.uuid.JsonUuidModule
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import java.io.IOException

/**
 * Jackson Json Library 가 제공하는 [JsonMapper] 를 제공합니다.
 *
 * @constructor Create empty Jackson
 */
object Jackson: KLogging() {

    /**
     * 기본 Jackson ObjectMapper
     */
    val defaultJsonMapper: JsonMapper by lazy { createDefaultJsonMapper() }

    val prettyJsonWriter: ObjectWriter by lazy { defaultJsonMapper.writerWithDefaultPrettyPrinter() }

    /**
     * 타입 정보를 제공하는 Jackson ObjectMapper
     */
    val typedJsonMapper: JsonMapper by lazy { createDefaultJsonMapper(needTypeInfo = true) }

    val prettyTypedJsonWriter: ObjectWriter by lazy { typedJsonMapper.writerWithDefaultPrettyPrinter() }


    fun createDefaultJsonMapper(needTypeInfo: Boolean = false): JsonMapper {
        log.info { "Create JsonMapper instance ... needTypeInfo=$needTypeInfo" }

        return jsonMapper {
            // Classpath에 있는 모든 Jackson용 Module을 찾아서 추가합니다.
            findAndAddModules()

            // 내부의 Module은 직접 등록합니다. (findAndRegisterModules() 에서 등록해주지 않는다)
            addModules(JsonUuidModule())

            // Serialization feature
            serializationInclusion(JsonInclude.Include.NON_NULL)
            enable(
                JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT,
                JsonGenerator.Feature.IGNORE_UNKNOWN,
                JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN,
            )
            disable(
                SerializationFeature.FAIL_ON_EMPTY_BEANS
            )

            // Deserialization feature
            enable(
                DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,
                DeserializationFeature.READ_ENUMS_USING_TO_STRING,
            )
            disable(FAIL_ON_IGNORED_PROPERTIES)

        }.apply {
            if (needTypeInfo) {
                activateDefaultTyping(
                    BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Any::class.java)
                        .allowIfSubTypeIsArray()
                        .build(),
                    ObjectMapper.DefaultTyping.EVERYTHING
                )
                initTypeInclusion(this)
            }
        }
    }

    private fun initTypeInclusion(mapper: JsonMapper) {
        val mapTypers = StdTypeResolverBuilder().apply {
            init(JsonTypeInfo.Id.CLASS, null)
            inclusion(JsonTypeInfo.As.PROPERTY)
        }
        mapper.setDefaultTyping(mapTypers)

        try {
            val s = mapper.writeValueAsBytes(1)
            mapper.readValue(s, Any::class.java)
        } catch (e: IOException) {
            throw IllegalStateException("JsonMapper에 타입정보 추가에 실패했습니다", e)
        }
    }
}
