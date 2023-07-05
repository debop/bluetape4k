package io.bluetape4k.openai.client.coroutines

import feign.RequestLine
import io.bluetape4k.openai.api.models.embedding.EmbeddingRequest
import io.bluetape4k.openai.api.models.embedding.EmbeddingResult

interface EmbeddingCoroutineApi {

    companion object {
        private const val BASE_PATH = "/v1/embeddings"
    }

    @RequestLine("POST $BASE_PATH")
    suspend fun createEmbeddings(request: EmbeddingRequest): EmbeddingResult
}
