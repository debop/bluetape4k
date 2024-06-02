package io.bluetape4k.openai.api.models.image

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import io.bluetape4k.openai.api.models.file.FileSource
import io.bluetape4k.support.requireNotNull
import java.io.Serializable

@BetaOpenAI
data class ImageVariationRequest(
    val n: Int? = 1,
    val size: ImageSize? = ImageSize.is1024x1024,
    val responseFormat: ImageResponseFormat? = ImageResponseFormat.url,
    val user: String? = null,
): Serializable {
    @get:JsonIgnore
    var imageFile: FileSource = FileSource.EMPTY

    @get:JsonProperty("image")
    val image: String by lazy { imageFile.toJson() }
}

@BetaOpenAI
fun imageVariationRequest(initializer: ImageVariationRequestBuilder.() -> Unit): ImageVariationRequest {
    return ImageVariationRequestBuilder().apply(initializer).build()
}

@BetaOpenAI
@OpenAIDsl
class ImageVariationRequestBuilder: ModelBuilder<ImageVariationRequest> {

    val imageFile: FileSource? = null
    val n: Int? = null
    val size: ImageSize? = null
    val responseFormat: ImageResponseFormat? = null
    val user: String? = null

    override fun build(): ImageVariationRequest {
        return ImageVariationRequest(
            n = n,
            size = size,
            responseFormat = responseFormat,
            user = user
        ).apply {
            this.imageFile = imageFile.requireNotNull("image")
        }
    }

}
