package io.bluetape4k.openai.api.models.finetune

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.api.models.ObjectId
import java.io.Serializable

/**
 * Fine tune event
 *
 * @property createdAt
 * @property level
 * @property message
 */
data class FineTuneEvent(
    @get:JsonProperty("object")
    val objectId: ObjectId? = null,
    val createdAt: Long,
    val level: String,
    val message: String,
): Serializable
