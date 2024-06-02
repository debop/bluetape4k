package io.bluetape4k.openai.api.models.image

import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import io.bluetape4k.support.requireNotBlank

/**
 * A request for OpenAi to create an image based on a prompt
 * All fields except prompt are optional
 *
 * 참고: [Images Create](https://beta.openai.com/docs/api-reference/images/create)
 *
 * @property prompt A text description of the desired image(s). The maximum length in 1000 characters.
 * @property n     The number of images to generate. Must be between 1 and 10. Defaults to 1.
 * @property size  The size of the generated images. Must be one of "256x256", "512x512", or "1024x1024". Defaults to "1024x1024".
 * @property responseFormat The format in which the generated images are returned. Must be one of url or b64_json. Defaults to url.
 * @property user  A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
 */
@BetaOpenAI
data class ImageCreationRequest(
    val prompt: String,
    val n: Int? = null,
    val size: ImageSize? = ImageSize.is1024x1024,
    val responseFormat: ImageResponseFormat? = ImageResponseFormat.url,
    val user: String? = null,
)

@BetaOpenAI
fun imageCreationRequest(initializer: ImageCreationRequestBuilder.() -> Unit): ImageCreationRequest =
    ImageCreationRequestBuilder().apply(initializer).build()

@BetaOpenAI
@OpenAIDsl
class ImageCreationRequestBuilder: ModelBuilder<ImageCreationRequest> {
    var prompt: String? = null
    var n: Int? = null
    var size: ImageSize? = null
    var responseFormat: ImageResponseFormat? = null
    var user: String? = null

    override fun build(): ImageCreationRequest = ImageCreationRequest(
        prompt = this.prompt.requireNotBlank("prompt"),
        n = this.n,
        size = this.size,
        responseFormat = this.responseFormat,
        user = this.user
    )
}
