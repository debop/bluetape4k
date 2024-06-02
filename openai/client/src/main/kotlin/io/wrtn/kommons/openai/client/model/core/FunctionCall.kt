package io.bluetape4k.openai.client.model.core

import com.fasterxml.jackson.annotation.JsonProperty

data class FunctionCall(
    @JsonProperty("name") val name: String,
    @JsonProperty("arguments") val arguments: String? = null,
)
