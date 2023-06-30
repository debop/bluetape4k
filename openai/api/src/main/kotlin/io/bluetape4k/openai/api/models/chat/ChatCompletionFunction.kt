package io.bluetape4k.openai.api.models.chat

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import java.io.Serializable
import java.util.function.Function

data class ChatCompletionFunction(
    val name: String,
    val description: String? = null,
    @JsonProperty("parameters")
    val parametersClass: Class<*>? = null,
): Serializable {

    @JsonIgnore
    var executor: Function<Any?, Any?>? = null
}

inline fun chatCompletionFunction(initializer: ChatCompletionFunctionBuilder.() -> Unit): ChatCompletionFunction =
    ChatCompletionFunctionBuilder().apply(initializer).build()

@OpenAIDsl
class ChatCompletionFunctionBuilder: ModelBuilder<ChatCompletionFunction> {

    var name: String? = null
    var description: String? = null
    private var parametersClass: Class<*>? = null
    private var executor: Function<Any?, Any?>? = null

    fun <T: Any?> executor(requestClass: Class<T>, executor: Function<T, Any?>) = apply {
        this.parametersClass = requestClass
        this.executor = executor as Function<Any?, Any?>
    }

    override fun build(): ChatCompletionFunction {
        return ChatCompletionFunction(
            name = name.requireNotBlank("name"),
            description = description,
            parametersClass = parametersClass
        ).also {
            it.executor = this.executor
        }
    }
}
