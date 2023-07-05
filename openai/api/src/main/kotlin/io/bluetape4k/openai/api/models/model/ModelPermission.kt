package io.bluetape4k.openai.api.models.model

import java.io.Serializable

data class ModelPermission(
    val id: String,
    val created: Long,
    val allowCreateEngine: Boolean,
    val allowSampling: Boolean,
    val allowLogprobs: Boolean,
    val allowSearchIndices: Boolean,
    val allowView: Boolean,
    val allowFineTuning: Boolean,
    val organization: String,
    val isBlocking: Boolean,
): Serializable
