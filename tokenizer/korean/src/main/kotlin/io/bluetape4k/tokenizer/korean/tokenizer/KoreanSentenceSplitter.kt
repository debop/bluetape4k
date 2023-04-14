package io.bluetape4k.tokenizer.korean.tokenizer

import java.io.Serializable

/**
 * Sentence Splitter
 */
object KoreanSentenceSplitter: Serializable {

    private val re =
        """
        |(?x)[^.!?…\s]   # First char is non-punct, non-ws
        |[^.!?…]*         # Greedily consume up to punctuation.
        |(?:              # Group for unrolling the loop.
        |[.!?…]         # (special) inner punctuation ok if
        |(?!['\"]?\s|$) # not followed by ws or EOS.
        |[^.!?…]*       # Greedily consume up to punctuation.
        |)*               # Zero or more (special normal*)
        |[.!?…]?          # Optional ending punctuation.
        |['\"]?           # Optional closing quote.
        |(?=\s|$)
        |"""
            .trimMargin()
            .toRegex()

    fun split(text: CharSequence): Sequence<Sentence> =
        re.findAll(text)
            .map { Sentence(it.groupValues[0], it.range.start, it.range.endInclusive + 1) }

}
