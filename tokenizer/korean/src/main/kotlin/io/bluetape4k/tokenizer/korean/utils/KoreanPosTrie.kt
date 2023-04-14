package io.bluetape4k.tokenizer.korean.utils

import java.io.Serializable

data class KoreanPosTrie(
    val curPos: KoreanPos?,
    val nextTrie: List<KoreanPosTrie>? = null,
    val ending: KoreanPos? = null,
): Serializable
