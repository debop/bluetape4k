package io.bluetape4k.openai.clients.retrofit2.sync

import io.bluetape4k.openai.api.models.ListResult
import io.bluetape4k.openai.api.models.model.Model
import io.bluetape4k.openai.api.models.model.ModelId
import retrofit2.http.GET
import retrofit2.http.Path

interface ModelSyncApi {

    companion object {
        private const val MODELS_PATH = "/v1/models"
    }

    @GET(MODELS_PATH)
    fun getModels(): ListResult<Model>

    @GET("$MODELS_PATH/{modelId}")
    fun getModel(@Path("modelId") modelId: ModelId)

}
