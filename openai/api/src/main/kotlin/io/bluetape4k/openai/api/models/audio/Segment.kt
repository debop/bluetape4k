package io.bluetape4k.openai.api.models.audio

import java.io.Serializable

data class Segment(
    val id: Int,
    val seek: Int,
    val start: Double,
    val end: Double,
    val text: String,
    val tokens: List<Int>,
    val temperature: Double,
    val avgLogprob: Double,
    val compressionRatio: Double,
    val noSpeechProb: Double,
    val transient: Boolean,
): Serializable
