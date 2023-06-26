package io.bluetape4k.openai.api.models.image

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import java.io.Serializable

@BetaOpenAI
data class ImageJSON(
    @JsonProperty("b64_json")
    val b64JSON: String,
): Serializable
