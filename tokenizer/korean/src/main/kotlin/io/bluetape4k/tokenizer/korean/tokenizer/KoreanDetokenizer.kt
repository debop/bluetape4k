package io.bluetape4k.tokenizer.korean.tokenizer

import io.bluetape4k.tokenizer.korean.utils.KoreanPos
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Eomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Josa
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Modifier
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Noun
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.PreEomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Punctuation
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Suffix
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Verb
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.VerbPrefix
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


/**
 * Detokenizes a list of tokenized words into a readable sentence.
 */
object KoreanDetokenizer {

    @JvmField
    val SuffixPos: Set<KoreanPos> = setOf(Josa, Eomi, PreEomi, Suffix, Punctuation)

    @JvmField
    val PrefixPos: Set<KoreanPos> = setOf(Modifier, VerbPrefix)

    suspend fun detokenize(input: Collection<String>): String {
        // Space guide prevents tokenizing a word that was not tokenized in the input.
        val spaceGuide = getSpaceGuide(input)

        // Tokenize a merged text with the space guide.
        val tokenized = KoreanTokenizer.tokenize(
            input.joinToString(""),
            TokenizerProfile(spaceGuide = spaceGuide)
        )

        // Attach suffixes and prefixes.
        // Attach Noun + Verb
        if (tokenized.isEmpty()) {
            return ""
        }
        return collapseTokens(tokenized).joinToString(" ")
    }

    private suspend fun collapseTokens(tokenized: List<KoreanToken>): List<String> {
        val output = mutableListOf<String>()
        var isPrefix = false
        var prevToken: KoreanToken? = null

        tokenized.asFlow().buffer()
            .onEach { token ->
                if (output.isNotEmpty() && (isPrefix || token.pos in SuffixPos)) {
                    val attached = output.last() + token.text
                    output[output.lastIndex] = attached
                    isPrefix = false
                    prevToken = token
                } else if (prevToken?.pos == Noun && token.pos == Verb) {
                    val attached = (output.lastOrNull() ?: "") + token.text
                    output[output.lastIndex] = attached
                    isPrefix = false
                    prevToken = token
                } else if (token.pos in PrefixPos) {
                    output.add(token.text)
                    isPrefix = true
                    prevToken = token
                } else {
                    output.add(token.text)
                    isPrefix = false
                    prevToken = token
                }
            }
            .collect()

        return output
    }

    private fun getSpaceGuide(input: Collection<String>): IntArray {
        val spaceGuid = IntArray(input.size)
        var len = 0
        input.forEachIndexed { index, word ->
            val length = len + word.length
            spaceGuid[index] = length
            len = length
        }
        return spaceGuid
    }
}
