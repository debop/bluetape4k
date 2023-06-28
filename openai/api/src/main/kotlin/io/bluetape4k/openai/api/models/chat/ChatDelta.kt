package io.bluetape4k.openai.api.models.chat

import io.bluetape4k.openai.api.annotations.BetaOpenAI
import java.io.Serializable

@BetaOpenAI
data class ChatDelta(
    val role: ChatRole? = null,
    val content: String? = null,
    val functionCall: FunctionCall? = null,
) : Serializable
