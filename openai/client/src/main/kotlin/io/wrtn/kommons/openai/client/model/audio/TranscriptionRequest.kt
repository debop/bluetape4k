package io.bluetape4k.openai.client.model.audio

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.File

/**
 * Transcription 은 form-data 로 전송해야 합니다.
 *
 * @property file
 * @property model
 * @property prompt
 * @property responseFormat
 * @property temperature
 * @property language
 * @constructor Create empty Transcription request
 */
data class TranscriptionRequest(
    @JsonProperty("file") val file: File,
    @JsonProperty("model") val model: String?,
    @JsonProperty("prompt") val prompt: String? = null,
    @JsonProperty("response_format") val responseFormat: String? = null,
    @JsonProperty("temperature") val temperature: Double? = null,
    @JsonProperty("language") val language: String? = null,
)
