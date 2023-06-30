package io.bluetape4k.openai.clients.retrofit2.sync

import io.bluetape4k.openai.api.models.completion.CompletionRequest
import io.bluetape4k.openai.api.models.completion.CompletionResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface CompletionSyncApi {

    companion object {
        internal const val COMPLETIONS_PATH = "/v1/completions"
    }

    @POST(COMPLETIONS_PATH)
    fun createCompletion(@Body request: CompletionRequest): CompletionResult

    @Streaming
    @POST(COMPLETIONS_PATH)
    fun createCompletionStream(@Body request: CompletionRequest): Call<ResponseBody>
}
