package io.bluetape4k.openai.clients.retrofit2.sync

import io.bluetape4k.openai.api.models.edits.EditRequest
import io.bluetape4k.openai.api.models.edits.EditResult
import retrofit2.http.Body
import retrofit2.http.POST

interface EditSyncApi {

    companion object {
        private const val EDITS_PATH = "/v1/edits"
    }

    @POST(EDITS_PATH)
    fun createEdit(@Body request: EditRequest): EditResult

}
