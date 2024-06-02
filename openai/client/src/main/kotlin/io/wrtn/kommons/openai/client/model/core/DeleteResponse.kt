package io.bluetape4k.openai.client.model.core

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 삭제 요청에 대한 응답
 *
 * @property id
 * @property objectType
 * @property deleted
 * @constructor Create empty Delete response
 */
data class DeleteResponse(
    @JsonProperty("id") val id: String,
    @JsonProperty("object") val objectType: String,
    @JsonProperty("deleted") val deleted: Boolean,
): Serializable
