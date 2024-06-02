package io.bluetape4k.tokenizer.korean.utils

import io.bluetape4k.logging.KLogging
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanToken
import io.bluetape4k.tokenizer.korean.utils.Hangul.composeHangul
import io.bluetape4k.tokenizer.korean.utils.Hangul.hasCoda
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Noun

/**
 * 한글 명사와 조사를 위한 Helper Class
 */
object KoreanSubstantive: KLogging() {

    private val JOSA_HEAD_FOR_CODA = setOf('은', '이', '을', '과', '아')
    private val JOSA_HEAD_FOR_NO_CODA = setOf('는', '가', '를', '와', '야', '여', '라')

    fun isJosaAttachable(prevChar: Char, headChar: Char): Boolean {
        return (hasCoda(prevChar) && headChar !in JOSA_HEAD_FOR_NO_CODA) ||
                (!hasCoda(prevChar) && headChar !in JOSA_HEAD_FOR_CODA)
    }

    //  fun isName(str: String): Boolean = isName(str as CharSequence)

    fun isName(chunk: CharSequence): Boolean {
        if (nameDictionaryContains("full_name", chunk) || nameDictionaryContains("given_name", chunk)) {
            return true
        }

        return when (chunk.length) {
            3    -> nameDictionaryContains("family_name", chunk[0].toString()) &&
                    nameDictionaryContains("given_name", chunk.subSequence(1, 3).toString())

            4    -> nameDictionaryContains("family_name", chunk.subSequence(0, 2).toString()) &&
                    nameDictionaryContains("given_name", chunk.subSequence(2, 4).toString())

            else -> false
        }
    }

    private val NUMBER_CHARS = "일이삼사오육칠팔구천백십해경조억만".map { it.code }.toSet()
    private val NUMBER_LAST_CHARS = "일이삼사오육칠팔구천백십해경조억만원배분초".map { it.code }.toSet()

    fun isKoreanNumber(chunk: CharSequence): Boolean =
        (0 until chunk.length).fold(true) { output, i ->
            if (i < chunk.length - 1) {
                output && NUMBER_CHARS.contains(chunk[i].code)
            } else {
                output && NUMBER_LAST_CHARS.contains(chunk[i].code)
            }
        }

    /**
     * Check if this chunk is an 'ㅇ' omitted variation of a noun (우혀니 -> 우현, 우현이, 빠순이 -> 빠순, 빠순이)
     *
     * @param chunk input chunk
     * @return true if the chunk is an 'ㅇ' omitted variation
     */
    fun isKoreanNameVariation(chunk: CharSequence): Boolean {
        // val nounDict = KoreanDictionaryProvider.koreanDictionary[Noun]!!

        if (isName(chunk)) return true

        val s = chunk.toString()
        if (s.length < 3 || s.length > 5) {
            return false
        }

        val decomposed: List<Hangul.HangulChar> = s.map(Hangul::decomposeHangul)
        val lastChar = decomposed.last()
        if (lastChar.onset !in Hangul.CODA_MAP.keys) return false
        if (lastChar.onset == 'ㅇ' || lastChar.vowel != 'ㅣ' || lastChar.coda != ' ') return false
        if (decomposed.init().last().coda != ' ') return false

        // Recover missing 'ㅇ' (우혀니 -> 우현, 우현이, 빠순이 -> 빠순, 빠순이)
        val recovered: String = decomposed.mapIndexed { i, hc ->
            when (i) {
                s.lastIndex     -> '이'
                s.lastIndex - 1 -> composeHangul(hc.copy(coda = decomposed.last().onset))
                else            -> composeHangul(hc)
            }
        }.joinToString("")

        return listOf(recovered, recovered.init()).any { isName(it) }
    }

    /**
     * Collapse all the one-char nouns into one unknown noun
     *
     * @param posNodes sequence of KoreanTokens
     * @return sequence of collapsed KoreanTokens
     */
    fun collapseNouns(posNodes: Iterable<KoreanToken>): List<KoreanToken> {
        val nodes = mutableListOf<KoreanToken>()
        var collapsing = false

        posNodes.forEach {
            if (it.pos == Noun && it.text.length == 1 && collapsing) {
                val text = nodes[0].text + it.text
                val offset = nodes[0].offset
                nodes[0] = KoreanToken(text, Noun, offset, text.length, unknown = true)
                collapsing = true
            } else if (it.pos == Noun && it.text.length == 1 && !collapsing) {
                nodes.add(0, it)
                collapsing = true
            } else {
                nodes.add(0, it)
                collapsing = false
            }
        }
        return nodes.reversed()
    }
}
