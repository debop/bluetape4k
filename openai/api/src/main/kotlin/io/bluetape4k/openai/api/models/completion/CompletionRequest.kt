package io.bluetape4k.openai.api.models.completion

import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

/**
 * A request for OpenAI to generate a predicted completion for a prompt.
 * All fields are Optional without [model]
 *
 * 참고: [create-completion](https://beta.openai.com/docs/api-reference/create-completion)
 */
data class CompletionRequest(
    val model: ModelId,
    val prompt: String? = null,
    val maxTokens: Int? = null,
    val temperature: Double? = null,
    val topP: Double? = null,
    val n: Int? = null,
    val logprobs: Int? = null,
    val echo: Boolean? = null,
    val stop: List<String>? = null,
    val presencePenalty: Double? = null,
    val frequencyPenalty: Double? = null,
    val bestOf: Int? = null,
    val logitBias: Map<String, Int>? = null,
    val user: String? = null,
    val suffix: String? = null,
): Serializable

inline fun completionRequest(initializer: CompletionRequestBuilder.() -> Unit): CompletionRequest =
    CompletionRequestBuilder().apply(initializer).build()

@OpenAIDsl
class CompletionRequestBuilder: ModelBuilder<CompletionRequest> {

    var model: ModelId? = null
    var prompt: String? = null
    var maxTokens: Int? = null
    var temperature: Double? = null
    var topP: Double? = null
    var n: Int? = null
    var logprobs: Int? = null
    var echo: Boolean? = null
    var stop: List<String>? = null
    var presencePenalty: Double? = null
    var frequencyPenalty: Double? = null
    var bestOf: Int? = null
    var logitBias: Map<String, Int>? = null
    var user: String? = null
    var suffix: String? = null

    override fun build(): CompletionRequest {
        return CompletionRequest(
            model = model.requireNotNull("model"),
            prompt = prompt,
            maxTokens = maxTokens,
            temperature = temperature,
            topP = topP,
            n = n,
            logprobs = logprobs,
            echo = echo,
            stop = stop,
            presencePenalty = presencePenalty,
            frequencyPenalty = frequencyPenalty,
            bestOf = bestOf,
            logitBias = logitBias,
            user = user,
            suffix = suffix
        )
    }
}
