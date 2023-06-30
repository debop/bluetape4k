package io.bluetape4k.openai.api.models.chat

import java.io.Serializable

data class ChatDelta(
    val role: ChatRole? = null,
    val content: String? = null,
    val functionCall: ChatFunctionCall? = null,
): Serializable
