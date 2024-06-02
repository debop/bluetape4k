package io.bluetape4k.openai.client.model.embeddnings

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.client.model.core.Usage

data class EmbeddingResponse(
    @JsonProperty("object") val objectType: String,
    @JsonProperty("model") val model: String,
    @JsonProperty("data") val data: List<Embedding> = emptyList(),
    @JsonProperty("usage") val usage: Usage? = null,
)
