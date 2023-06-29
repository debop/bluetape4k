package io.bluetape4k.openai.api.models.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.api.models.ListResult
import io.bluetape4k.openai.api.models.ObjectId
import java.io.Serializable

typealias ModelResultList = ListResult<ModelResult>

data class ModelResult(
    val id: ModelId,
    @get:JsonProperty(value = "object")
    val objectId: ObjectId? = null,
    val ownedBy: String? = null,
    val permission: List<ModelPermission>? = null,
): Serializable
