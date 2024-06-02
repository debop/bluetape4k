package io.bluetape4k.openai.client.model.core

/**
 * Func
 *
 * [Functions](https://platform.openai.com/docs/api-reference/chat/create#functions)
 *
 * @property name
 * @property parameters
 * @property description
 * @constructor Create empty Func
 */
data class Func(
    val name: String,
    val parameters: String,
    val description: String? = null,
)
