package io.bluetape4k.openai.api.models.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JvmInline
value class ModelId(val id: String) : Serializable

data class Model(
    val id: ModelId,
    @get:JsonProperty(value = "object")
    val objectName: String? = null,
    val ownedBy: String? = null,
    val permission: List<ModelPermission>? = null,
) : Serializable

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
) : Serializable
