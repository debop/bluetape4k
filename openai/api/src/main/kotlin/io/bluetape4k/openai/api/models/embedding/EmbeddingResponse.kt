package io.bluetape4k.openai.api.models.embedding

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.api.models.Usage
import java.io.Serializable

/**
 * Embeddings response
 *
 * @property embeddings An embedding results.
 * @property usage Emdedding usage data.
 */
data class EmbeddingResponse(
    @get:JsonProperty("data")
    val embeddings: List<Embedding>,
    val usage: Usage,
) : Serializable
