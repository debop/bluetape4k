package io.bluetape4k.openai.api.models.chat

import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import java.io.Serializable

@BetaOpenAI
data class ChatMessage(
    val role: ChatRole,
    val content: String? = null,
    val name: String? = null,
    val functionCall: FunctionCall? = null,
): Serializable

@BetaOpenAI
inline fun chatMessage(builder: ChatMessageBuilder.() -> Unit): ChatMessage =
    ChatMessageBuilder().apply(builder).build()

@BetaOpenAI
@OpenAIDsl
class ChatMessageBuilder: ModelBuilder<ChatMessage> {

    var role: ChatRole? = null
    var content: String? = null
    var name: String? = null
    var functionCall: FunctionCall? = null

    override fun build(): ChatMessage = ChatMessage(
        role = role.requireNotNull("role"),
        content = content,
        name = name,
        functionCall = functionCall
    )
}
