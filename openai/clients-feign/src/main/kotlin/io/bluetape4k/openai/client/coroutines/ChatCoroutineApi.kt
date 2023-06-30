package io.bluetape4k.openai.client.coroutines

import feign.RequestLine
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.models.chat.ChatCompletionRequest
import io.bluetape4k.openai.api.models.chat.ChatCompletionResult
import kotlinx.coroutines.flow.Flow

interface ChatCoroutineApi {

    companion object {
        private const val BASE_PATH = "/v1/chat/completions"
    }

    @BetaOpenAI
    @RequestLine("POST $BASE_PATH")
    suspend fun createChatCompletion(request: ChatCompletionRequest): ChatCompletionResult

    @BetaOpenAI
    @RequestLine("POST $BASE_PATH")
    suspend fun createChatCompletions(request: ChatCompletionRequest): Flow<ChatCompletionResult>
}
