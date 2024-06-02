package io.bluetape4k.openai.api.models.image

import com.fasterxml.jackson.annotation.JsonIgnore
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import io.bluetape4k.openai.api.models.file.FileSource
import io.bluetape4k.support.requireNotBlank
import io.bluetape4k.support.requireNotNull

@BetaOpenAI
data class ImageEditRequest(
    val prompt: String,
    val n: Int? = 1,
    val size: ImageSize? = ImageSize.is1024x1024,
    val responseFormat: ImageResponseFormat? = ImageResponseFormat.url,
    val user: String? = null,
) {
    // TODO: File 정보를 Octect Stream 형태로 보내주어야 한다.
    @get:JsonIgnore
    var imageFile: FileSource = FileSource.EMPTY

    @get:JsonIgnore
    var maskFile: FileSource? = null

    val image: String by lazy { imageFile.toJson() }
    val mask: String? by lazy { maskFile?.toJson() }
}

@BetaOpenAI
fun imageEdit(initializer: ImageEditRequestBuilder.() -> Unit): ImageEditRequest {
    return ImageEditRequestBuilder().apply(initializer).build()
}

@BetaOpenAI
@OpenAIDsl
class ImageEditRequestBuilder: ModelBuilder<ImageEditRequest> {

    var imageFile: FileSource? = null
    var maskFile: FileSource? = null
    var prompt: String? = null
    var n: Int? = 1
    var size: ImageSize? = ImageSize.is1024x1024
    var responseFormat: ImageResponseFormat? = ImageResponseFormat.url
    var user: String? = null

    override fun build(): ImageEditRequest {
        return ImageEditRequest(
            prompt = prompt.requireNotBlank("prompt"),
            n = n,
            size = size,
            responseFormat = responseFormat,
            user = user
        ).apply {
            this.imageFile = imageFile.requireNotNull("imageFile")
            this.maskFile = maskFile
        }
    }
}
