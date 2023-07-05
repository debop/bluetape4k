package io.bluetape4k.openai.clients.retrofit2.sync

import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.models.audio.TranscriptionRequest
import io.bluetape4k.openai.api.models.audio.TranscriptionResult
import io.bluetape4k.openai.api.models.audio.TranslationRequest
import io.bluetape4k.openai.api.models.audio.TranslationResult
import retrofit2.http.Body
import retrofit2.http.POST

interface AudioSyncApi {

    companion object {
        private const val AUDIO_PATH = "/v1/audio"
    }

    @BetaOpenAI
    @POST("$AUDIO_PATH/transcriptions")
    fun transcription(@Body request: TranscriptionRequest): TranscriptionResult

    @BetaOpenAI
    @POST("$AUDIO_PATH/translations")
    fun translation(@Body request: TranslationRequest): TranslationResult
}
