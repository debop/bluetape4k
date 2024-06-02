package io.bluetape4k.openai.client.model.chat

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.client.model.core.FunctionCall

data class ChatDelta(
    @JsonProperty("role") val role: String? = null,
    @JsonProperty("content") val content: String? = null,
    @JsonProperty("function_call") val functionCall: FunctionCall? = null,
)
