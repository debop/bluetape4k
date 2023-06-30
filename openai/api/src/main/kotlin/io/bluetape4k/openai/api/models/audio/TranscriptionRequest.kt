package io.bluetape4k.openai.api.models.audio

import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import io.bluetape4k.openai.api.models.file.FileSource
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

/**
 * Request to transcribe audio into the input [language].
 *
 * @property audio
 * @property model
 * @property prompt
 * @property responseFormat
 * @property temperature
 * @property language
 */
data class TranscriptionRequest(
    val audio: FileSource,
    val model: ModelId,
    val prompt: String? = null,
    val responseFormat: String? = null,
    val temperature: Double? = null,
    val language: String? = null,
): Serializable

@OpenAIDsl
class TranscriptionRequestBuilder: ModelBuilder<TranscriptionRequest> {

    var audio: FileSource? = null
    var model: ModelId? = null
    var prompt: String? = null
    var responseFormat: String? = null
    var temperature: Double? = null
    var language: String? = null

    override fun build(): TranscriptionRequest {
        return TranscriptionRequest(
            audio = audio.requireNotNull("audio"),
            model = model.requireNotNull("model"),
            prompt = prompt,
            responseFormat = responseFormat,
            temperature = temperature,
            language = language
        )
    }
}

inline fun transcriptionRequest(builder: TranscriptionRequestBuilder.() -> Unit): TranscriptionRequest =
    TranscriptionRequestBuilder().apply(builder).build()
