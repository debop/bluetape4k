package io.bluetape4k.openai.client.model.completions

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.client.model.core.Usage

data class CompletionResponse(
    @JsonProperty("id") val id: String,
    @JsonProperty("object") val objectType: String,
    @JsonProperty("created") val created: Long,
    @JsonProperty("model") val model: String,
    @JsonProperty("choices") val choices: List<Choice> = emptyList(),
    @JsonProperty("usage") val usage: Usage? = null,
)
