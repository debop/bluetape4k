package io.bluetape4k.openai.api.models.finetune

import java.io.Serializable

/**
 * Fine tune event
 *
 * @property createdAt
 * @property level
 * @property message
 */
data class FineTuneEvent(
    val createdAt: Long,
    val level: String,
    val message: String,
) : Serializable
