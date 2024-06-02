package io.bluetape4k.openai.client.api

import io.bluetape4k.openai.client.model.audio.TranscriptionResponse
import io.bluetape4k.openai.client.model.audio.TranslationResponse
import io.bluetape4k.openai.client.model.chat.ChatCompletionRequest
import io.bluetape4k.openai.client.model.chat.ChatCompletionResponse
import io.bluetape4k.openai.client.model.completions.CompletionRequest
import io.bluetape4k.openai.client.model.completions.CompletionResponse
import io.bluetape4k.openai.client.model.core.DeleteResponse
import io.bluetape4k.openai.client.model.core.ListResponse
import io.bluetape4k.openai.client.model.edit.EditRequest
import io.bluetape4k.openai.client.model.edit.EditResponse
import io.bluetape4k.openai.client.model.embeddnings.EmbeddingRequest
import io.bluetape4k.openai.client.model.embeddnings.EmbeddingResponse
import io.bluetape4k.openai.client.model.file.File
import io.bluetape4k.openai.client.model.finetune.FineTuneEvent
import io.bluetape4k.openai.client.model.finetune.FineTuneRequest
import io.bluetape4k.openai.client.model.finetune.FineTuneResponse
import io.bluetape4k.openai.client.model.images.CreateImageRequest
import io.bluetape4k.openai.client.model.images.ImageResponse
import io.bluetape4k.openai.client.model.model.Model
import io.bluetape4k.openai.client.model.moderation.ModerationRequest
import io.bluetape4k.openai.client.model.moderation.ModerationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

interface OpenAiApi {

    companion object {
        private const val AUDIO_PATH = "/v1/audio"
        private const val CHAT_COMPLETIONS_PATH = "/v1/chat/completions"
        private const val COMPLETION_PATH = "/v1/completions"
        private const val EDIT_PATH = "/v1/edits"
        private const val EMBEDDING_PATH = "/v1/embeddings"
        private const val FILE_PATH = "/v1/files"
        private const val FINE_TUNE_PATH = "/v1/fine-tunes/jobs"
        private const val IMAGE_PATH = "/v1/images"
        private const val MODEL_PATH = "/v1/models"
        private const val MODERATION_PATH = "/v1/moderations"
    }

    @GET(MODEL_PATH)
    suspend fun getModels(): ListResponse<Model>

    @GET("$MODEL_PATH/{model_id}")
    suspend fun getModel(@Path("model_id") modelId: String): Model

    @POST(CHAT_COMPLETIONS_PATH)
    suspend fun createChatCompletion(@Body request: ChatCompletionRequest): ChatCompletionResponse

    @Streaming
    @POST(CHAT_COMPLETIONS_PATH)
    suspend fun createChatCompletionStream(@Body request: ChatCompletionRequest): ResponseBody


    @POST(COMPLETION_PATH)
    suspend fun createCompletion(@Body request: CompletionRequest): CompletionResponse

    @Streaming
    @POST(COMPLETION_PATH)
    suspend fun createCompletionStream(@Body request: CompletionRequest): ResponseBody


    @POST(COMPLETION_PATH)
    fun createFineTuneCompletion(@Body request: CompletionRequest): CompletionResponse

    @POST(EDIT_PATH)
    suspend fun createEdit(@Body request: EditRequest): EditResponse

    @POST(EMBEDDING_PATH)
    suspend fun createEmbeddings(@Body request: EmbeddingRequest): EmbeddingResponse

    /**
     * Get files
     */
    @GET(FILE_PATH)
    suspend fun getFiles(): ListResponse<File>

    /**
     * Upload file content
     */
    @Multipart
    @POST(FILE_PATH)
    suspend fun uploadFile(
        @Part("purpose") purpose: RequestBody,
        @Part file: MultipartBody.Part,
    ): File

    @DELETE("$FILE_PATH/{file_id}")
    suspend fun deleteFile(@Path("file_id") fileId: String): DeleteResponse

    /**
     * Get file information
     */
    @GET("$FILE_PATH/{file_id}")
    suspend fun retrieveFile(@Path("file_id") fileId: String): File

    /**
     * Download file
     */
    @GET("$FILE_PATH/{file_id}/content")
    suspend fun retrieveFileContent(@Path("file_id") fileId: String): ByteArray

    @POST(FINE_TUNE_PATH)
    suspend fun createFineTune(@Body request: FineTuneRequest): FineTuneResponse

    @GET(FINE_TUNE_PATH)
    suspend fun getFineTunes(): ListResponse<FineTuneResponse>

    @GET("$FINE_TUNE_PATH/{fine_tune_job_id}")
    suspend fun getFineTune(@Path("fine_tune_job_id") fineTuneJobId: String): FineTuneResponse

    @POST("$FINE_TUNE_PATH/{fine_tune_job_id}/cancel")
    suspend fun cancelFineTune(@Path("fine_tune_job_id") fineTuneJobId: String): FineTuneResponse

    @GET("$FINE_TUNE_PATH/{fine_tune_job_id}/events")
    suspend fun getFineTuneEvents(@Path("fine_tune_job_id") fineTuneJobId: String): ListResponse<FineTuneEvent>

    @POST("$IMAGE_PATH/generations")
    suspend fun createImageGeneration(@Body request: CreateImageRequest): ImageResponse

    @POST("$IMAGE_PATH/edits")
    suspend fun createImageEdit(@Body requestBody: RequestBody): ImageResponse

    @POST("$IMAGE_PATH/variations")
    suspend fun createImageVariation(@Body requestBody: RequestBody): ImageResponse

    @POST("$AUDIO_PATH/transcriptions")
    suspend fun createAudioTranscription(@Body requestBody: RequestBody): TranscriptionResponse

    @POST("$AUDIO_PATH/translations")
    suspend fun createAudioTranslation(@Body requestBody: RequestBody): TranslationResponse

    @POST(MODERATION_PATH)
    suspend fun createModeration(@Body request: ModerationRequest): ModerationResponse
}
