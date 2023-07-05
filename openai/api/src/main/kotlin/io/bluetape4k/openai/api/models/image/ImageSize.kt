package io.bluetape4k.openai.api.models.image

import io.bluetape4k.openai.api.annotations.BetaOpenAI

/**
 * The size of the generated images.
 */
@BetaOpenAI
@JvmInline
value class ImageSize(val size: String) {

    companion object {

        /**
         * Image size of dimension 256x256
         */
        val is256x256: ImageSize = ImageSize("256x256")

        /**
         * Image size of dimension 512x512
         */
        val is512x512: ImageSize = ImageSize("512x512")

        /**
         * Image size of dimension 1024x1024
         */
        val is1024x1024: ImageSize = ImageSize("1024x1024")
    }
}
