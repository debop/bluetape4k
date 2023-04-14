package io.bluetape4k.tokenizer.korean.phrase

import io.bluetape4k.tokenizer.korean.tokenizer.KoreanToken
import io.bluetape4k.tokenizer.korean.utils.KoreanPos
import java.io.Serializable

data class KoreanPhrase(
    val tokens: List<KoreanToken>,
    val pos: KoreanPos = KoreanPos.Noun,
): Serializable {

    val offset get() = this.tokens.first().offset

    val text get() = this.tokens.map { it.text }.joinToString("")

    val length get() = this.tokens.map { it.text.length }.sum()

    override fun toString(): String =
        "${this.text}($pos: ${this.offset}, ${this.length})"
}
