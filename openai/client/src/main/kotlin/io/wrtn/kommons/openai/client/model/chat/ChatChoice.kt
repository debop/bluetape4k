package io.bluetape4k.openai.client.model.chat

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatChoice(
    @JsonProperty("index") val index: Int,
    @JsonProperty("message") val messages: ChatMessage? = null,
    @JsonProperty("finish_reason") val finishReason: String? = null,
)
