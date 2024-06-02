package io.bluetape4k.openai.clients.jackson

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.json.JsonMapper
import io.bluetape4k.json.jackson.Jackson
import io.bluetape4k.openai.api.models.chat.ChatCompletionFunction
import io.bluetape4k.openai.api.models.chat.ChatCompletionRequest
import io.bluetape4k.openai.api.models.chat.ChatFunctionCall

object OpenAIJackson {

    /**
     * OpenAI 용 [JsonMapper] 를 제공합니다.
     */
    fun defaultJsonMapper(): JsonMapper {
        val mapper = Jackson.defaultJsonMapper.copy()

        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)

        mapper.addMixIn(ChatCompletionFunction::class.java, ChatCompletionFunctionMixIn::class.java)
        mapper.addMixIn(ChatCompletionRequest::class.java, ChatCompletionRequestMixIn::class.java)
        mapper.addMixIn(ChatFunctionCall::class.java, ChatFunctionCallMixIn::class.java)

        return mapper
    }
}
