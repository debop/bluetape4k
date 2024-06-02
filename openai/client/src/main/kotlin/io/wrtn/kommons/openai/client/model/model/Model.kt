package io.bluetape4k.openai.client.model.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class Model(
    @JsonProperty("id") val id: String,
    @JsonProperty("object") val objectType: String,
    @JsonProperty("created") val created: Long,
    @JsonProperty("owned_by") val ownedBy: String,
): Serializable
