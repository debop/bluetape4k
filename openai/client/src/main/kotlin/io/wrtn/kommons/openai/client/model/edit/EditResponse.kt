package io.bluetape4k.openai.client.model.edit

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.client.model.core.Usage


data class EditResponse(
    @JsonProperty("object") val objectType: String,
    @JsonProperty("created") val created: Long? = null,
    @JsonProperty("choices") val choices: List<Choice>? = null,
    @JsonProperty("usage") val usage: Usage? = null,
)
