package io.bluetape4k.openai.clients.jackson

import com.fasterxml.jackson.databind.annotation.JsonSerialize

abstract class ChatCompletionFunctionMixIn {

    @JsonSerialize(using = ChatFunctionParametersSerializer::class)
    abstract fun getParametersClass(): Class<*>
}
