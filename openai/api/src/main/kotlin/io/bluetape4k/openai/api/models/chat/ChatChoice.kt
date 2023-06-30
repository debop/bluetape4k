package io.bluetape4k.openai.api.models.chat

import java.io.Serializable

data class ChatChoice(
    val index: Int? = null,
    val message: ChatMessage? = null,
    val finishReason: String? = null,
): Serializable
