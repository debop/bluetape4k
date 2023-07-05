package io.bluetape4k.openai.api.models.chat

import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import java.io.Serializable

data class ChatMessage(
    val role: ChatRole,
    val content: String? = null,
    val name: String? = null,
    val functionCall: ChatFunctionCall? = null,
): Serializable

inline fun chatMessage(builder: ChatMessageBuilder.() -> Unit): ChatMessage =
    ChatMessageBuilder().apply(builder).build()

@OpenAIDsl
class ChatMessageBuilder: ModelBuilder<ChatMessage> {

    var role: ChatRole? = null
    var content: String? = null
    var name: String? = null
    var functionCall: ChatFunctionCall? = null

    override fun build(): ChatMessage = ChatMessage(
        role = role.requireNotNull("role"),
        content = content,
        name = name,
        functionCall = functionCall
    )
}
