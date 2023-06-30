package io.bluetape4k.openai.api.models.moderation

import java.io.Serializable

/**
 * An object containing a response from the moderation api
 *
 * 참고: [Moderations Create](https://beta.openai.com/docs/api-reference/moderations/create)
 *
 * @property id A unique id assigned to this moderation
 * @property model The model used.
 * @property results A list of moderation scores.
 */
data class ModerationResult(
    val id: String,
    val model: ModerationModel,
    val results: List<Moderation>,
): Serializable
