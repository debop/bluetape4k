package io.bluetape4k.openai.clients.retrofit2.sync

import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.models.chat.ChatCompletionRequest
import io.bluetape4k.openai.api.models.chat.ChatCompletionResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ChatSyncApi {

    companion object {
        private const val CHAT_COMPLETIONS_PATH = "/v1/chat/completions"
    }

    @BetaOpenAI
    @POST(CHAT_COMPLETIONS_PATH)
    fun createChatCompletion(@Body request: ChatCompletionRequest): ChatCompletionResult

    @BetaOpenAI
    @Streaming
    @POST(CHAT_COMPLETIONS_PATH)
    fun createChatCompletionStream(@Body request: ChatCompletionRequest): Call<ResponseBody>
}
