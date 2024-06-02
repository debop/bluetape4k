package io.bluetape4k.openai.client.model.completions

import com.fasterxml.jackson.annotation.JsonProperty

data class Choice(
    @JsonProperty("text") val text: String,
    @JsonProperty("index") val index: Int,
    @JsonProperty("logprobs") val logprobs: Logprobs? = null,
    @JsonProperty("finish_reason") val finishReason: String? = null,
)
