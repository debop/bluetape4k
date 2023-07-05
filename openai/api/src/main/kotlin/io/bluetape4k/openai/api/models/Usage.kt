package io.bluetape4k.openai.api.models

import java.io.Serializable

/**
 * Usage
 *
 * @property promptTokens       count of prompts tokens
 * @property completionTokens   count of completion tokens
 * @property totalTokens        count of total tokens
 */
data class Usage(
    val promptTokens: Int? = null,
    val completionTokens: Int? = null,
    val totalTokens: Int? = null,
): Serializable
