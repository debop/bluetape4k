package io.bluetape4k.openai.client.model.edit

import com.fasterxml.jackson.annotation.JsonProperty

data class EditRequest(
    @JsonProperty("model") val model: String,
    @JsonProperty("instruction") val instruction: String,
    @JsonProperty("input") val input: String? = null,
    @JsonProperty("n") val n: Int = 1,
    @JsonProperty("temperature") val temperature: Double = 1.0,
    @JsonProperty("top_p") val topP: Double = 1.0,
)
