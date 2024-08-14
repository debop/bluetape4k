package io.bluetape4k.tokenizer.korean.stemmer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanToken
import io.bluetape4k.tokenizer.korean.utils.KoreanDictionaryProvider
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adjective
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Eomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.PreEomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Verb
import java.util.*


/**
 * Stems Adjectives and Verbs: 새로운 스테밍을 추가했었다. -> 새롭다 + 스테밍 + 을 + 추가 + 하다
 */
object KoreanStemmer: KLogging() {

    @JvmField
    val Endings = setOf(Eomi, PreEomi)

    @JvmField
    val Predicates = setOf(Verb, Adjective)

    @JvmField
    val EndingForNouns = setOf("하다", "되다", "없다")


    /**
     * Removes Ending tokens recovering the root form of predicates
     *
     * @param tokens A sequence of tokens
     * @return A sequence of collapsed Korean tokens
     */
    fun stem(tokens: List<KoreanToken>): List<KoreanToken> {
        if (tokens.isEmpty()) {
            return tokens
        }
        if (!tokens.any { it.pos in Predicates }) {
            return tokens
        }

        val stemmed = LinkedList<KoreanToken>()

        tokens
            .onEach { token ->
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
                } else if (token.pos in Predicates) {
                    val stem = KoreanDictionaryProvider.predicateStems[token.pos]?.get(token.text)
                    // val token1 = token.copy(stem = stem)
                    // log.trace { "동사, 형용사 활용: text=${token.text}, stem=$stem : $token -> $token1" }
                    stemmed.add(0, token.copy(stem = stem))
                } else {
                    log.trace { "not found stem for $token" }
                    stemmed.add(0, token)
                }
            }

        return stemmed.reversed()
    }
}
