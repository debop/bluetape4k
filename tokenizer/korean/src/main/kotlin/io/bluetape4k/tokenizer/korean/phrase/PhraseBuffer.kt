package io.bluetape4k.tokenizer.korean.phrase

import io.bluetape4k.tokenizer.korean.utils.KoreanPos
import io.bluetape4k.tokenizer.korean.utils.KoreanPosTrie
import java.io.Serializable

data class PhraseBuffer(
    val phrases: List<KoreanPhrase>,
    val curTrie: List<KoreanPosTrie?>,
    val ending: KoreanPos?,
): Serializable
