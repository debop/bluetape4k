package io.bluetape4k.openai.api.models.audio

import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import io.bluetape4k.openai.api.models.file.FileSource
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

/**
 * Request to translate an audio into english.
 *
 * @property audio
 * @property model
 * @property prompt
 * @property responseFormat
 * @property temperature
 * @constructor Create empty TranslationResult request
 */
@BetaOpenAI
data class TranslationRequest(
    /**
     * The audio file to translate, in one of these formats: mp3, mp4, mpeg, mpga, m4a, wav, or webm.
     */
    val audio: FileSource,
    /**
     * ID of the model to use. Only `whisper-1` is currently available.
     */
    val model: ModelId,

    /**
     * An optional text to guide the model's style or continue a previous audio segment.
     * The [prompt](https://platform.openai.com/docs/guides/speech-to-text/prompting) should be in English.
     */
    val prompt: String? = null,

    /**
     * The format of the transcript output, in one of these options: json, text, srt, verbose_json, or vtt.
     *
     * Default: json
     */
    val responseFormat: String? = null,

    /**
     * The sampling temperature, between 0 and 1. Higher values like 0.8 will make the output more random, while lower
     * values like 0.2 will make it more focused and deterministic. If set to 0, the model will use
     * [log probability](https://en.wikipedia.org/wiki/Log_probability) to automatically increase the temperature until
     * certain thresholds are hit.
     *
     * Default: 0
     */
    val temperature: Double? = null,
): Serializable

@BetaOpenAI
@OpenAIDsl
class TranslationRequestBuilder: ModelBuilder<TranslationRequest> {

    var audio: FileSource? = null
    var model: ModelId? = null
    var prompt: String? = null
    var responseFormat: String? = null
    var temperature: Double? = null

    override fun build(): TranslationRequest {
        return TranslationRequest(
            audio = audio.requireNotNull("audio"),
            model = model.requireNotNull("model"),
            prompt = prompt,
            responseFormat = responseFormat,
            temperature = temperature,
        )
    }
}

/**
 * Creates a translation request ([TranslationRequest])
 */
@BetaOpenAI
inline fun translationRequest(builder: TranslationRequestBuilder.() -> Unit): TranslationRequest =
    TranslationRequestBuilder().apply(builder).build()
