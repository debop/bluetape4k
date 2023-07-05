package io.bluetape4k.openai.client.coroutines

import feign.RequestLine
import io.bluetape4k.openai.api.models.completion.CompletionRequest
import io.bluetape4k.openai.api.models.completion.CompletionResult
import kotlinx.coroutines.flow.Flow

interface CompletionCoroutineApi {

    companion object {
        private const val BASE_PATH = "/v1/completions"
    }

    @RequestLine("POST /v1/completions")
    suspend fun createCompletion(request: CompletionRequest): CompletionResult

    // Feign 은 SSE 를 직접 받지 못한다. 
    // https://github.com/OpenFeign/feign/blob/master/reactive/README.md
    @RequestLine("POST /v1/completions")
    suspend fun createCompletions(request: CompletionRequest): Flow<CompletionResult>


}
