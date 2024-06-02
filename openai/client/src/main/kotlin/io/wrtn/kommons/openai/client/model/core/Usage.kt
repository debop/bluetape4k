package io.bluetape4k.openai.client.model.core

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 요청에 사용된 OpenAI 리소스
 *
 * @property promptTokens      사용된 prompt token 수
 * @property completionTokens  사용된 completions token 수
 * @property totalTokens       사용된 전체 token 수
 */
data class Usage(
    @JsonProperty("prompt_tokens") val promptTokens: Long? = null,
    @JsonProperty("completion_tokens") val completionTokens: Long? = null,
    @JsonProperty("total_tokens") val totalTokens: Long? = null,
): Serializable
