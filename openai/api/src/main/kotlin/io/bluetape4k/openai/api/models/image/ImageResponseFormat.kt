package io.bluetape4k.openai.api.models.image

import io.bluetape4k.openai.api.annotations.BetaOpenAI

@BetaOpenAI
@JvmInline
value class ImageResponseFormat(val format: String) {

    companion object {

        /**
         * Response format as url.
         */
        val url: ImageResponseFormat = ImageResponseFormat("url")

        /**
         * Response format as base 64 json.
         */
        val base64Json: ImageResponseFormat = ImageResponseFormat("b64_json")
    }
}
