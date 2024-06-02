package io.bluetape4k.openai.client.model.images

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateImageRequest(
    @JsonProperty("prompt") val prompt: String,
    @JsonProperty("n") val n: Int? = null,
    @JsonProperty("size") val size: String? = null, // 256x256, 512x512, 1024x1024
    @JsonProperty("response_format") val responseFormat: String? = null, // url, b64_json
    @JsonProperty("user") val user: String? = null,
)
