package io.bluetape4k.openai.api.models.embedding

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.api.models.ObjectId
import java.io.Serializable

data class Embedding(
    @JsonProperty("object")
    val objectId: ObjectId? = null,
    val embedding: List<Double>,
    val index: Int,
): Serializable
