package io.bluetape4k.openai.clients.retrofit2.sync

import io.bluetape4k.openai.api.models.moderation.ModerationRequest
import io.bluetape4k.openai.api.models.moderation.ModerationResult
import retrofit2.http.Body
import retrofit2.http.POST

interface ModerationSyncApi {

    companion object {
        private const val MODERATIONS_PATH = "/v1/moderations"
    }

    @POST(MODERATIONS_PATH)
    fun createModeration(@Body request: ModerationRequest): ModerationResult
}
