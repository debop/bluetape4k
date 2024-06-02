package io.bluetape4k.openai.client.model.embeddnings

import com.fasterxml.jackson.annotation.JsonProperty

data class Embedding(
    @JsonProperty("index") val index: Int,
    @JsonProperty("object") val objectType: String,
    @JsonProperty("embedding") val embedding: List<Float>,
)
