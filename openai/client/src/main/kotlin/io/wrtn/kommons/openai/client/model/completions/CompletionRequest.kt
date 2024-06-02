package io.bluetape4k.openai.client.model.completions

import com.fasterxml.jackson.annotation.JsonProperty

data class CompletionRequest(
    @JsonProperty("model") val model: String,
    @JsonProperty("prompt") val prompt: List<String> = emptyList(),
    @JsonProperty("suffix") val suffix: String? = null,
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
