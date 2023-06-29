package io.bluetape4k.openai.api.models.edits

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.api.models.ObjectId
import io.bluetape4k.openai.api.models.Usage
import io.bluetape4k.openai.api.models.completion.Choice
import java.io.Serializable

/**
 * Response to the edit creation request.
 *
 * @property created   The creation time in epoch milliseconds.
 * @property choices   A list of generated completions.
 * @property usage     EditResult usage data.
 */
data class EditResult(
    @get:JsonProperty("object")
    val objectId: ObjectId? = null,
    val created: Long,
    val choices: List<Choice>,
    val usage: Usage,
): Serializable
