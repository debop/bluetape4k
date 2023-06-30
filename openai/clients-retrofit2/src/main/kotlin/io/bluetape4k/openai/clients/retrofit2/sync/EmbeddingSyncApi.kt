package io.bluetape4k.openai.clients.retrofit2.sync

import io.bluetape4k.openai.api.models.embedding.EmbeddingRequest
import io.bluetape4k.openai.api.models.embedding.EmbeddingResult
import retrofit2.http.Body
import retrofit2.http.POST

interface EmbeddingSyncApi {

    companion object {
        private const val EMBEDDINGS_PATH = "/v1/embeddings"
    }

    @POST(EMBEDDINGS_PATH)
    fun createEmbeddings(@Body request: EmbeddingRequest): EmbeddingResult
}
