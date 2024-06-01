package io.bluetape4k.json.jackson.uuid

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside

/**
 * 실제 UUID 형식의 Identifier 를 Base62 로 인코딩하거나 단순 문자열로 전달할 수 있도록 합니다.
 *
 * ```
 * data class Message(
 *
 *     @field:JsonUuidEncoder(JsonUuidEncoderType.BASE62)
 *     val id: UUID
 * )
 * ```
 *
 * @property value
 * @constructor Create empty Json uuid format
 */
@JacksonAnnotationsInside
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonUuidEncoder(
    val value: JsonUuidEncoderType = JsonUuidEncoderType.BASE62,
)
