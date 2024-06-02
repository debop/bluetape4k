package io.bluetape4k.tokenizer.korean.phrase

import io.bluetape4k.support.unsafeLazy
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanToken
import io.bluetape4k.tokenizer.korean.utils.KoreanPos
import java.io.Serializable

data class KoreanPhrase(
    val tokens: List<KoreanToken>,
    val pos: KoreanPos = KoreanPos.Noun,
): Serializable {

    val offset: Int get() = this.tokens[0].offset

    val text by unsafeLazy {
        this.tokens.joinToString("") { it.text }
    }

    val length by unsafeLazy {
        this.tokens.sumOf { it.text.length }
    }

    override fun toString(): String =
        "${this.text}($pos: ${this.offset}, ${this.length})"
}
