package io.bluetape4k.openai.client.model.finetune

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.client.model.core.ListResponse

typealias FineTuneEventList = ListResponse<FineTuneEvent>

data class FineTuneEvent(
    @JsonProperty("object") val objectType: String,
    @JsonProperty("id") val id: String,
    @JsonProperty("created_at") val createdAt: Long? = null,
    @JsonProperty("level") val level: String? = null,
    @JsonProperty("message") val message: String? = null,
    @JsonProperty("data") val data: String? = null,
    @JsonProperty("type") val type: String? = null,
)
