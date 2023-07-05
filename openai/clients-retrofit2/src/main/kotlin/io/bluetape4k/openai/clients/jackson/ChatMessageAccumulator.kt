package io.bluetape4k.openai.clients.jackson

import io.bluetape4k.openai.api.models.chat.ChatFunctionCall
import io.bluetape4k.openai.api.models.chat.ChatMessage

/**
 * Class that accumulates chat messages and provides utility methods for
 * handling message chunks and function calls within a chat stream. This
 * class is immutable.
 */
class ChatMessageAccumulator(
    val messageChunk: ChatMessage,
    val accumulatedMessage: ChatMessage,
) {

    val isFunctionCall: Boolean
        get() = accumulatedMessage.functionCall?.name != null

    val isChatMessage: Boolean get() = !isFunctionCall

    val chatFunctionCallChunk: ChatFunctionCall?
        get() = messageChunk.functionCall

    val accumulatedChatFunctionCall: ChatFunctionCall?
        get() = accumulatedMessage.functionCall
}
