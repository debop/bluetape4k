package io.bluetape4k.openai.client.model.core

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * OpenAI engine 과 search endpoint 의 응답을 담는 data class
 *
 * @param T
 * @property objectType 응답 객체의 타입
 * @property data 응답 객체의 리스트
 */
data class ListResponse<T>(

    @JsonProperty("object") val objectType: String? = null,

    /**
     * A list containing the requested data.
     */
    @JsonProperty("data") val data: List<T> = emptyList(),

    @JsonProperty("created") val created: Long? = null,

    @JsonProperty("has_more") val hasMore: Boolean? = null,
): Serializable
