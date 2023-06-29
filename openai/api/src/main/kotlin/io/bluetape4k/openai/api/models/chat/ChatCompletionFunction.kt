package io.bluetape4k.openai.api.models.chat

import com.fasterxml.jackson.annotation.JsonIgnore
import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import java.io.Serializable
import java.util.function.Function

@BetaOpenAI
data class ChatCompletionFunction(
    val name: String,
    val description: String? = null,
    val parameters: Class<*>? = null,
): Serializable {

    @JsonIgnore
    var executor: Function<Any?, Any?>? = null
}

@BetaOpenAI
inline fun chatCompletionFunction(initializer: ChatCompletionFunctionBuilder.() -> Unit): ChatCompletionFunction =
    ChatCompletionFunctionBuilder().apply(initializer).build()

@BetaOpenAI
@OpenAIDsl
class ChatCompletionFunctionBuilder: ModelBuilder<ChatCompletionFunction> {

    var name: String? = null
    var description: String? = null
    private var parameters: Class<*>? = null
    private var executor: Function<Any?, Any?>? = null

    fun <T: Any?> executor(requestClass: Class<T>, executor: Function<T, Any?>) = apply {
        this.parameters = requestClass
        this.executor = executor as Function<Any?, Any?>
    }

    override fun build(): ChatCompletionFunction {
        return ChatCompletionFunction(
            name = name.requireNotBlank("name"),
            description = description,
            parameters = parameters
        ).also {
            it.executor = this.executor
        }
    }
}
