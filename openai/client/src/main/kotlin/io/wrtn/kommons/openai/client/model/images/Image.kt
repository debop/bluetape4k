package io.bluetape4k.openai.client.model.images

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.client.model.core.ListResponse

typealias ImageList = ListResponse<Image>

/**
 * Image
 *
 * @property url
 * @property b64Json
 */
data class Image(
    @JsonProperty("url") val url: String? = null,
    @JsonProperty("b64_json") val b64Json: String? = null,
)
