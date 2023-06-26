package io.bluetape4k.openai.api.models.image

import io.bluetape4k.openai.api.annotations.BetaOpenAI
import java.io.Serializable

@BetaOpenAI
data class ImageURL(
    val url: String,
): Serializable
