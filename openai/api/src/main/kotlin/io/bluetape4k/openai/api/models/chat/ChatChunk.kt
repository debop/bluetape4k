package io.bluetape4k.openai.api.models.chat

import io.bluetape4k.openai.api.annotations.BetaOpenAI
import java.io.Serializable

@BetaOpenAI
data class ChatChunk(
    val index: Int? = null,
    val delta: ChatDelta? = null,
    val finishReason: String? = null,
) : Serializable
