package io.bluetape4k.openai.client.mixin

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.bluetape4k.openai.client.model.core.FunctionCall

abstract class ChatCompletionRequestMixIn {

    @JsonSerialize(using = ChatCompletionRequestSerDe.Serializer::class)
    @JsonDeserialize(using = ChatCompletionRequestSerDe.Deserializer::class)
    abstract fun getFunctionCall(): FunctionCall
}
