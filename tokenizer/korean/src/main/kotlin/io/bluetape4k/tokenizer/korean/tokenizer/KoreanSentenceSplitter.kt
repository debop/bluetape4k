package io.bluetape4k.tokenizer.korean.tokenizer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import java.io.Serializable

/**
 * Sentence Splitter
 */
object KoreanSentenceSplitter: Serializable {

    private val re: Regex =
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

    fun split(text: CharSequence): Flow<Sentence> {
        return re
            .findAll(text)
            .asFlow()
            .map { mr ->
                Sentence(mr.groupValues[0], mr.range.first, mr.range.last + 1)
            }
    }

}
