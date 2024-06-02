package io.bluetape4k.openai.client.model.embeddnings

import com.fasterxml.jackson.annotation.JsonProperty

data class EmbeddingRequest(
    @JsonProperty("model") val model: String,
    @JsonProperty("input") val input: List<String>,
    @JsonProperty("user") val user: String? = null,
)
