package io.bluetape4k.openai.api.models.image

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.openai.api.OpenAIDsl
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import java.io.Serializable

@BetaOpenAI
data class ImageCreation(
    val prompt: String,
    val n: Int? = null,
    val size: ImageSize? = null,
    val user: String? = null,
): Serializable

@BetaOpenAI
fun imageCreation(initializer: ImageCreationBuilder.() -> Unit): ImageCreation =
    ImageCreationBuilder().apply(initializer).build()

@BetaOpenAI
@OpenAIDsl
class ImageCreationBuilder {
    var prompt: String? = null
    var n: Int? = null
    var size: ImageSize? = null
    var user: String? = null

    fun build(): ImageCreation = ImageCreation(
        prompt = this.prompt.requireNotBlank("prompt"),
        n = this.n,
        size = this.size,
        user = this.user
    )
}
