package io.bluetape4k.openai.client.model.chat

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.client.model.core.Usage

data class ChatCompletionResponse(
    @JsonProperty("id") val id: String,
    @JsonProperty("object") val objectType: String,
    @JsonProperty("created") val created: Long,
    @JsonProperty("model") val model: String? = null,
    @JsonProperty("choices") val choices: List<ChatChoice>,
    @JsonProperty("usage") val usage: Usage? = null,
)
