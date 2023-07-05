package io.bluetape4k.openai.api.models.audio

import java.io.Serializable

/**
 * Create translation response.
 *
 * [text] format depends on [TranslationRequest]'s `responseFormat`.
 * Remaining field are provided only in case of response format `verbose_json`.
 */
data class TranslationResult(
    val task: String? = null,
    val language: String? = null,
    val duration: Double? = null,
    val segments: List<Segment>? = null,
    val text: String,
): Serializable
