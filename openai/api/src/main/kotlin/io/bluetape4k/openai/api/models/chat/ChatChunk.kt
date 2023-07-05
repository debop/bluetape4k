package io.bluetape4k.openai.api.models.chat

import java.io.Serializable

data class ChatChunk(
    val index: Int? = null,
    val message: ChatDelta? = null,
    val finishReason: String? = null,
): Serializable
