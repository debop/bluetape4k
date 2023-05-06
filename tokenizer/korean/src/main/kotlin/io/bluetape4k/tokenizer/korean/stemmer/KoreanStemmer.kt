package io.bluetape4k.tokenizer.korean.stemmer

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.eclipse.unifiedSetOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanToken
import io.bluetape4k.tokenizer.korean.utils.KoreanDictionaryProvider
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adjective
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Eomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.PreEomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Verb
import java.io.Serializable


/**
 * Stems Adjectives and Verbs: 새로운 스테밍을 추가했었다. -> 새롭다 + 스테밍 + 을 + 추가 + 하다
 */
object KoreanStemmer: KLogging(), Serializable {

    @JvmField
    val Endings = unifiedSetOf(Eomi, PreEomi)

    @JvmField
    val Predicates = unifiedSetOf(Verb, Adjective)

    @JvmField
    val EndingForNouns = unifiedSetOf("하다", "되다", "없다")


    /**
     * Removes Ending tokens recovering the root form of predicates
     *
     * @param tokens A sequence of tokens
     * @return A sequence of collapsed Korean tokens
     */
    fun stem(tokens: List<KoreanToken>): List<KoreanToken> {
        if (!tokens.any { Predicates.contains(it.pos) }) {
            return tokens
        }

        val stemmed = fastListOf<KoreanToken>()

        tokens.forEach { token ->
            if (stemmed.isNotEmpty() && Endings.contains(token.pos)) {
                if (Predicates.contains(stemmed.first().pos)) {
                    val prevToken = stemmed.first()
                    val token1 = prevToken.copy(
                        text = prevToken.text + token.text,
                        length = prevToken.length + token.length
                    )
                    stemmed[0] = token1
                } else {
                    stemmed.add(0, token)
                }
            } else if (Predicates.contains(token.pos)) {
                val token1 = token.copy(stem = KoreanDictionaryProvider.predicateStems[token.pos]?.get(token.text))
                stemmed.add(0, token1)
            } else {
                stemmed.add(0, token)
            }
        }
        return stemmed.reverseThis()
    }
}
