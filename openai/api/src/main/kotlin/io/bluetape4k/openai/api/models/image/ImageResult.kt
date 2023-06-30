package io.bluetape4k.openai.api.models.image

data class ImageResult(
    val data: List<Image>,
    val created: Long,
)
