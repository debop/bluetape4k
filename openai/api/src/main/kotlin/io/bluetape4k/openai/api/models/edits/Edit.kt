package io.bluetape4k.openai.api.models.edits

import io.bluetape4k.openai.api.models.Usage
import io.bluetape4k.openai.api.models.completion.Choice
import java.io.Serializable

/**
 * Response to the edit creation request.
 *
 * @property created   The creation time in epoch milliseconds.
 * @property choices   A list of generated completions.
 * @property usage     Edit usage data.
 */
data class Edit(
    val created: Long,
    val choices: List<Choice>,
    val usage: Usage,
) : Serializable
