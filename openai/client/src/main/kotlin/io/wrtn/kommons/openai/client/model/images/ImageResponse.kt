package io.bluetape4k.openai.client.model.images

import com.fasterxml.jackson.annotation.JsonProperty

data class ImageResponse(
    @JsonProperty("created") val created: Long? = null,
    @JsonProperty("data") val data: List<Image> = emptyList(),
)
