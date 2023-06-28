package io.bluetape4k.openai.api.models.completion

import java.io.Serializable

/**
 * Log probabilities of different token options?
 * Returned if [CompletionRequest.logprobs] is greater than zero.
 *
 * 참고: [create-completion](https://beta.openai.com/docs/api-reference/create-completion)
 *
 * @property tokens          The tokens chosen by the completion api
 * @property tokenLogprobs   The log probability of each token in [tokens]
 * @property topLogprobs     A map for each index in the completion result.
 *                           The map contains the top [CompletionRequest.logprobs] tokens and their probabilities
 * @property textOffset      The character offset from the start of the returned text for each of the chosen tokens.
 */
data class Logprobs(
    val tokens: List<String>,
    val tokenLogprobs: List<Double>,
    val topLogprobs: List<Map<String, Double>>,
    val textOffset: List<Int>,
) : Serializable
