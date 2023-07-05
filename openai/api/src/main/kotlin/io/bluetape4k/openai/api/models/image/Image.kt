package io.bluetape4k.openai.api.models.image

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * An object containing either a URL or a base 64 encoded image.
 *
 * https://beta.openai.com/docs/api-reference/images
 */
data class Image(
    val url: String? = null,

    @JsonProperty("b64_json")
    val b64Json: String? = null,
): Serializable
