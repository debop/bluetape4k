package io.bluetape4k.openai.client.model.audio

import com.fasterxml.jackson.annotation.JsonProperty

data class TranslationResponse(
    @JsonProperty("text") val text: String? = null,
)
