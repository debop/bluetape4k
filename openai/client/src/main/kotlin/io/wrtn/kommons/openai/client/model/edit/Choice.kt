package io.bluetape4k.openai.client.model.edit

import com.fasterxml.jackson.annotation.JsonProperty

data class Choice(
    @JsonProperty("text") val text: String,
    @JsonProperty("index") val index: Int,
)
