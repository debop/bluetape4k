package io.bluetape4k.openai.api.models.moderation

import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.support.requireNotNull
import java.io.Serializable

/**
 * Request to classify if text violates OpenAI's Content Policy.
 *
 * @property input The input text to classify.
 * @property model Moderation model. Defaults to [ModerationModel.Latest].
 */
data class ModerationRequest(
    val input: String,
    val model: ModerationModel? = ModerationModel.Default,
): Serializable

inline fun moderationRequest(initializer: ModerationRequestBuilder.() -> Unit): ModerationRequest =
    ModerationRequestBuilder().apply(initializer).build()

/**
 * Data class representing a ModerationRequest
 */
@OpenAIDsl
class ModerationRequestBuilder {

    /**
     * The input text to classify.
     */
    var input: String? = null

    /**
     * Moderation model. Defaults to [ModerationModel.Latest].
     */
    var model: ModerationModel? = null

    fun build(): ModerationRequest = ModerationRequest(
        input = input.requireNotNull("input"),
        model = model ?: ModerationModel.Default
    )
}
