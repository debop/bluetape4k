package io.bluetape4k.openai.api.models.chat

import io.bluetape4k.openai.api.annotations.BetaOpenAI
import java.io.Serializable

@BetaOpenAI
data class ChatChoice(
    val index: Int? = null,
    val message: ChatMessage? = null,
    val finishReason: String? = null,
) : Serializable
