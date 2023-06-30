package io.bluetape4k.openai.client.coroutines

import feign.RequestLine
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.models.audio.TranscriptionRequest
import io.bluetape4k.openai.api.models.audio.TranscriptionResult
import io.bluetape4k.openai.api.models.audio.TranslationRequest
import io.bluetape4k.openai.api.models.audio.TranslationResult

interface AudioCoroutineApi {

    companion object {
        private const val BASE_PATH = "/v1/audio"
    }

    @BetaOpenAI
    @RequestLine("GET $BASE_PATH/transcriptions")
    suspend fun transcription(request: TranscriptionRequest): TranscriptionResult

    @BetaOpenAI
    @RequestLine("GET $BASE_PATH/translations")
    suspend fun translation(request: TranslationRequest): TranslationResult
}
