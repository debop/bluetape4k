package io.bluetape4k.openai.client.model.core

data class APIError(
    val error: APIErrorDetails,
) {

    data class APIErrorDetails(
        val code: String? = null,
        val message: String? = null,
        // https://platform.openai.com/docs/guides/error-codes/python-library-error-types
        val type: String? = null,
        val param: String? = null,
    )
}
