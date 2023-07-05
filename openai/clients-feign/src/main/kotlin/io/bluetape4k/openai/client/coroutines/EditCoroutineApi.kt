package io.bluetape4k.openai.client.coroutines

import feign.RequestLine
import io.bluetape4k.openai.api.models.edits.EditRequest
import io.bluetape4k.openai.api.models.edits.EditResult

interface EditCoroutineApi {

    companion object {
        private const val BASE_PATH = "/v1/edits"
    }

    @RequestLine("POST $BASE_PATH")
    suspend fun createEdit(request: EditRequest): EditResult
}
