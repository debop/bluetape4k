package io.bluetape4k.openai.clients.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

abstract class ChatFunctionCallMixIn {

    @JsonSerialize(using = ChatFunctionCallArgumentsSerde.Serializer::class)
    @JsonDeserialize(using = ChatFunctionCallArgumentsSerde.Deserializer::class)
    abstract fun getArguments(): JsonNode
}
