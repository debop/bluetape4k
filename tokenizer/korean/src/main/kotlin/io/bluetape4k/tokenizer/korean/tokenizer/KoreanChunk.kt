package io.bluetape4k.tokenizer.korean.tokenizer

import java.io.Serializable

data class KoreanChunk(
    val text: String,
    val offset: Int,
    val length: Int,
): Serializable
