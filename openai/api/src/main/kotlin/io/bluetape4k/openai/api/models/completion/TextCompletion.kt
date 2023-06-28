package io.bluetape4k.openai.api.models.completion

import io.bluetape4k.openai.api.models.Usage
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

/**
 * An object containing a response from the completion api.
 *
 * 참고: [create-completion](https://beta.openai.com/docs/api-reference/create-completion)
 *
 * @property id       A unique id assigned to this completion
 * @property created  The creation time in epoch milliseconds.
 * @property model    The GPT-3 model used
 * @property choices  A list of generated completions
 * @property usage    Text completion usage data.
 */
data class TextCompletion(
    val id: String,
    val created: Long,
    val model: ModelId,
    val choices: List<Choice>,
    val usage: Usage? = null,
) : Serializable
