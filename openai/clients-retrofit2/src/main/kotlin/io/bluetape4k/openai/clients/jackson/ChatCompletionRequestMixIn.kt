package io.bluetape4k.openai.clients.jackson

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.models.chat.ChatFunctionMode

abstract class ChatCompletionRequestMixIn {

    @BetaOpenAI
    @JsonSerialize(using = ChatFunctionModeSerde.Serializer::class)
    @JsonDeserialize(using = ChatFunctionModeSerde.Deserializer::class)
    abstract fun getChatFunctionMode(): ChatFunctionMode

}
