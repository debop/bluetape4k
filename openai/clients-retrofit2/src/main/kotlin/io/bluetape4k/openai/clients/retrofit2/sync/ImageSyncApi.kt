package io.bluetape4k.openai.clients.retrofit2.sync

import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.models.image.ImageCreationRequest
import io.bluetape4k.openai.api.models.image.ImageEditRequest
import io.bluetape4k.openai.api.models.image.ImageResult
import io.bluetape4k.openai.api.models.image.ImageVariationRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ImageSyncApi {

    companion object {
        private const val IMAGES_PATH = "v1/images"
    }

    @BetaOpenAI
    @POST("$IMAGES_PATH/generations")
    fun imageGenerations(@Body request: ImageCreationRequest): ImageResult

    @BetaOpenAI
    @POST("$IMAGES_PATH/edits")
    fun imageEdits(@Body request: ImageEditRequest): ImageResult

    @BetaOpenAI
    @POST("$IMAGES_PATH/variations")
    fun imageVariations(@Body request: ImageVariationRequest): ImageResult

}
