package io.bluetape4k.openai.api.models.embedding

import java.io.Serializable

data class Embedding(
    val embedding: List<Double>,
    val index: Int,
) : Serializable
