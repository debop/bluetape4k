package io.bluetape4k.openai.api.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * Response as List of [T]
 *
 * @param T
 * @property data
 * @property usage
 */
data class ListResult<T>(
    val data: List<T>,
    val usage: Usage? = null,
    @get:JsonProperty("object")
    val objectId: ObjectId? = null,
): Serializable
