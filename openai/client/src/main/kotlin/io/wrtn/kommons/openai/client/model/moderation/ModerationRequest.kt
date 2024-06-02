package io.bluetape4k.openai.client.model.moderation

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class ModerationRequest(
    @JsonProperty("input") val input: List<String>,
    @JsonProperty("model") val model: String? = null,
): Serializable
