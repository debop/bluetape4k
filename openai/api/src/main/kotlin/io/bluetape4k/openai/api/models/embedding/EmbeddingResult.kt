package io.bluetape4k.openai.api.models.embedding

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.api.models.ObjectId
import io.bluetape4k.openai.api.models.Usage
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

/**
 * Embeddings response
 *
 * @property embeddings An embedding results.
 * @property usage Emdedding usage data.
 */
data class EmbeddingResult(
    @get:JsonProperty("object")
    val objectId: ObjectId? = null,
    val data: List<Embedding>,
    val model: ModelId,
    val usage: Usage,
): Serializable
