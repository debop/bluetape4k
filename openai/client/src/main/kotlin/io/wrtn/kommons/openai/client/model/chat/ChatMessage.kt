package io.bluetape4k.openai.client.model.chat

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.client.model.core.FunctionCall

/**
 * Chat message
 *
 * @property role    Must be either 'system', 'user', 'assistant' or 'function'. You may use [ChatMessageRole] enum.
 * @property content
 * @property name
 * @property functionCall
 * @constructor Create empty Chat message
 */
data class ChatMessage(

    @JsonProperty("role") val role: String,

    @JsonProperty("content") @JsonInclude val content: String? = null,

    @JsonProperty("name") val name: String? = null,

    @JsonProperty("function_call") val functionCall: FunctionCall? = null,
)
