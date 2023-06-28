package io.bluetape4k.openai.api.models.chat

import io.bluetape4k.openai.api.annotations.BetaOpenAI

@BetaOpenAI
@JvmInline
value class ChatRole(val role: String) {

    companion object {
        val System: ChatRole = ChatRole("system")
        val User: ChatRole = ChatRole("user")
        val Assistant: ChatRole = ChatRole("assistant")
        val Function: ChatRole = ChatRole("function")
    }
}
