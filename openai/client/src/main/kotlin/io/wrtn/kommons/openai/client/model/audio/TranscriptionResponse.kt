package io.bluetape4k.openai.client.model.audio

import com.fasterxml.jackson.annotation.JsonProperty

data class TranscriptionResponse(
    @JsonProperty("text") val text: String? = null,
)
