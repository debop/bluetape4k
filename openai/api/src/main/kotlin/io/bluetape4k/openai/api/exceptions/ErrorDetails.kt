package io.bluetape4k.openai.api.exceptions

import java.io.Serializable

data class OpenAIError(
    val detail: OpenAIErrorDetails? = null,
): Serializable

data class OpenAIErrorDetails(
    val code: String? = null,
    val message: String? = null,
    val param: String? = null,
    val type: String? = null,
): Serializable
