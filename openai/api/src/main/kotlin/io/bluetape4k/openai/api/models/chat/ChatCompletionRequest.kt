package io.bluetape4k.openai.api.models.chat

import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

@BetaOpenAI
data class ChatCompletionRequest(
    val model: ModelId,
    val messages: List<ChatMessage>,
    val temperature: Double? = null,
    val topP: Double? = null,
    val n: Int? = null,
    val stream: Boolean? = null,
    val stop: List<String>? = null,
    val maxTokens: Int? = null,
    val presentPenalty: Double? = null,
    val frequencyPenalty: Double? = null,
    val logitBias: Map<String, Int>? = null,
    val user: String? = null,
    val functions: List<ChatCompletionFunction>? = null,
    val functionCall: FunctionMode? = null,
): Serializable

@BetaOpenAI
fun chatCompletionRequest(initializer: ChatCompletionRequstBuilder.() -> Unit): ChatCompletionRequest =
    ChatCompletionRequstBuilder().apply(initializer).build()

@BetaOpenAI
@OpenAIDsl
class ChatCompletionRequstBuilder: ModelBuilder<ChatCompletionRequest> {

    var model: ModelId? = null
    internal var messages: List<ChatMessage>? = null
    var temperature: Double? = null
    var topP: Double? = null
    var n: Int? = null
    var stream: Boolean? = null
    var stop: List<String>? = null
    var maxTokens: Int? = null
    var presentPenalty: Double? = null
    var frequencyPenalty: Double? = null
    var logitBias: Map<String, Int>? = null
    var user: String? = null
    internal var functions: List<ChatCompletionFunction>? = null
    var functionCall: FunctionMode? = null

    fun messages(initializer: ChatMessagesBuilder.() -> Unit) {
        messages = ChatMessagesBuilder().apply(initializer).messages
    }

    fun functions(initializer: ChatCompletionFunctionsBuilder.() -> Unit) {
        functions = ChatCompletionFunctionsBuilder().apply(initializer).functions
    }

    override fun build(): ChatCompletionRequest {
        return ChatCompletionRequest(
            model = model.requireNotNull("model"),
            messages = messages.requireNotNull("messages"),
            temperature = temperature,
            topP = topP,
            n = n,
            stream = stream,
            stop = stop,
            maxTokens = maxTokens,
            presentPenalty = presentPenalty,
            frequencyPenalty = frequencyPenalty,
            logitBias = logitBias,
            user = user,
            functions = functions
        )
    }
}

@BetaOpenAI
class ChatMessagesBuilder {
    internal val messages = mutableListOf<ChatMessage>()

    fun message(initializer: ChatMessageBuilder.() -> Unit) {
        messages.add(chatMessage(initializer))
    }
}

@BetaOpenAI
class ChatCompletionFunctionsBuilder {
    internal val functions = mutableListOf<ChatCompletionFunction>()

    fun function(initializer: ChatCompletionFunctionBuilder.() -> Unit) {
        functions.add(chatCompletionFunction(initializer))
    }
}
