package io.bluetape4k.openai.client.coroutines

import feign.Param
import feign.RequestLine
import io.bluetape4k.openai.api.models.model.Model
import io.bluetape4k.openai.api.models.model.ModelId
import kotlinx.coroutines.flow.Flow

interface ModelCoroutineApi {

    companion object {
        private const val BASE_PATH = "/v1/models"
    }

    @RequestLine("GET $BASE_PATH")
    fun models(): Flow<Model>

    @RequestLine("GET $BASE_PATH/{modelId}")
    suspend fun model(@Param("modelId") modelId: ModelId): Model
}
