package io.bluetape4k.openai.client.model.chat

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatChunk(
    @JsonProperty("index") val index: Int,
    @JsonProperty("delta") val delta: ChatDelta? = null,
    @JsonProperty("finish_reason") val finishReason: String? = null,
)
