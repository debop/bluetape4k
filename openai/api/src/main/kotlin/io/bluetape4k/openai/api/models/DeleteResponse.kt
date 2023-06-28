package io.bluetape4k.openai.api.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 삭제 작업에 대한 응답
 *
 * @property id
 * @property objectType
 * @property deleted
 */
data class DeleteResponse(
    val id: String,

    @get:JsonProperty("object")
    val objectType: String,

    val deleted: Boolean,
): Serializable
