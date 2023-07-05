package io.bluetape4k.openai.clients.retrofit2.sync

import io.bluetape4k.openai.api.models.DeleteResult
import io.bluetape4k.openai.api.models.ListResult
import io.bluetape4k.openai.api.models.completion.CompletionRequest
import io.bluetape4k.openai.api.models.completion.CompletionResult
import io.bluetape4k.openai.api.models.finetune.FineTuneEvent
import io.bluetape4k.openai.api.models.finetune.FineTuneId
import io.bluetape4k.openai.api.models.finetune.FineTuneRequest
import io.bluetape4k.openai.api.models.finetune.FineTuneResult
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FineTuneSyncApi {

    companion object {
        private const val FINE_TUNES_PATH = "/v1/fine-tunes"
        private const val MODELS_PATH = "/v1/models"
    }

    @GET(FINE_TUNES_PATH)
    fun getFineTunes(): ListResult<FineTuneResult>

    @POST(FINE_TUNES_PATH)
    fun createFineTune(@Body request: FineTuneRequest): FineTuneResult

    // TODO: 이건 굳이 또 만들 필요가 있나?
    @POST(CompletionSyncApi.COMPLETIONS_PATH)
    fun createFineTuneCompletion(@Body request: CompletionRequest): CompletionResult

    @GET("$FINE_TUNES_PATH/{fineTuneId}")
    fun getFineTune(@Path("fineTuneId") fineTuneId: FineTuneId): FineTuneResult

    @GET("$FINE_TUNES_PATH/{fineTuneId}/cancel")
    fun cancelFineTune(@Path("fineTuneId") fineTuneId: FineTuneId): FineTuneResult

    @GET("$FINE_TUNES_PATH/{fineTuneId}/events")
    fun getFineTuneEvents(@Path("fineTuneId") fineTuneId: FineTuneId): ListResult<FineTuneEvent>

    @DELETE("$MODELS_PATH/{fineTuneId}")
    fun deleteFineTune(@Path("fineTuneId") fineTuneId: FineTuneId): DeleteResult

}
