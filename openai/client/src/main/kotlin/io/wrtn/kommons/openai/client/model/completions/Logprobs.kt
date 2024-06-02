package io.bluetape4k.openai.client.model.completions

import com.fasterxml.jackson.annotation.JsonProperty

data class Logprobs(
    @JsonProperty("tokens") val tokens: List<String> = emptyList(),
    @JsonProperty("token_logprobs") val tokenLogprobs: List<Double> = emptyList(),
    @JsonProperty("top_logprobs") val topLogprobs: List<Map<String, Double>> = emptyList(),
    @JsonProperty("text_offset") val textOffset: List<Int> = emptyList(),
)
