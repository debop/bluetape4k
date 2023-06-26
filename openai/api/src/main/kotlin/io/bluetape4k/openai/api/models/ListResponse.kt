package io.bluetape4k.openai.api.models

import java.io.Serializable

/**
 * Response as List of [T]
 *
 * @param T
 * @property data
 * @property usage
 */
data class ListResponse<T>(
    val data: List<T>,
    val usage: Usage? = null,
): Serializable
