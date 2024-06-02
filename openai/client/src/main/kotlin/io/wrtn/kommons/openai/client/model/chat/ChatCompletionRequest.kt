package io.bluetape4k.openai.client.model.chat

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.client.model.core.Func
import io.bluetape4k.openai.client.model.core.FunctionCall

/**
 * Chat completions request
 *
 * [Create Chat Completion API](https://platform.openai.com/docs/api-reference/chat/create)
 *
 * @property model
 * @property messages
 * @property functions
 * @constructor Create empty Chat completions request
 */
data class ChatCompletionRequest(
    @JsonProperty("model") val model: String,
    @JsonProperty("messages") val messages: List<ChatMessage> = emptyList(),
    @JsonProperty("functions") val functions: List<Func> = emptyList(),
    @JsonProperty("function_call") val functionCall: FunctionCall? = null,
    @JsonProperty("max_tokens") val maxTokens: Int? = null,
    @JsonProperty("temperature") val temperature: Double? = null,
    @JsonProperty("top_p") val topP: Double? = null,
    @JsonProperty("n") val n: Int? = null,
    @JsonProperty("stream") val stream: Boolean? = null,
    @JsonProperty("logprobs") val logprobs: Int? = null,
    @JsonProperty("echo") val echo: Boolean? = null,
    @JsonProperty("stop") val stop: List<String>? = null,
    @JsonProperty("presence_penalty") val presencePenalty: Double? = null,
    @JsonProperty("frequency_penalty") val frequencyPenalty: Double? = null,
    @JsonProperty("best_of") val bestOf: Int? = null,
    @JsonProperty("logit_bias") val logitBias: Map<String, Double>? = null,
    @JsonProperty("user") val user: String? = null,
)
