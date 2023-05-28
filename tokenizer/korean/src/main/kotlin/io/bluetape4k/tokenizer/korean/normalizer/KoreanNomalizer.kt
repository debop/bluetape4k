package io.bluetape4k.tokenizer.korean.normalizer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.sliding
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanTokenizer
import io.bluetape4k.tokenizer.korean.utils.Hangul
import io.bluetape4k.tokenizer.korean.utils.Hangul.HangulChar
import io.bluetape4k.tokenizer.korean.utils.Hangul.composeHangul
import io.bluetape4k.tokenizer.korean.utils.Hangul.decomposeHangul
import io.bluetape4k.tokenizer.korean.utils.KoreanDictionaryProvider
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adverb
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Conjunction
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Eomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Noun
import io.bluetape4k.tokenizer.korean.utils.KoreanPosx
import io.bluetape4k.tokenizer.korean.utils.koreanContains
import kotlinx.coroutines.runBlocking
import java.io.Serializable
import java.util.regex.MatchResult
import java.util.regex.Matcher

/**
 * Normalize Korean colloquial text (한글 구어체를 Normailze 합니다)
 */
object KoreanNormalizer: KLogging(), Serializable {

    private val EXTENTED_KOREAN_REGEX: Regex = """([ㄱ-ㅣ가-힣]+)""".toRegex()
    private val KOREAN_TO_NORMALIZE_REGEX: Regex = """([가-힣]+)(ㅋ+|ㅎ+|[ㅠㅜ]+)""".toRegex()
    private val REPEATING_CHAR_REGEX: Regex = """(.)\1{3,}|[ㅠㅜ]{3,}""".toRegex()
    private val REPEATING_2CHAR_REGEX: Regex = """(..)\1{2,}""".toRegex()
    private val REPEATING_3CHAR_REGEX: Regex = """(...)\1{2,}""".toRegex()

    private val WHITESPACE_REGEX: Regex = """\s+""".toRegex()

    private val CODA_N_EXCPETION: CharArray = "은는운인텐근른픈닌든던".toCharArray()
    private val CODA_N_LAST_CHAR: CharArray = charArrayOf('데', '가', '지')

    private data class Segment(val text: String, val matchData: MatchResult?)

    suspend fun normalize(input: CharSequence): CharSequence {
        return EXTENTED_KOREAN_REGEX.replace(input) { m ->
            runBlocking {
                normalizeKoreanChunk(m.groupValues.first())
            }
        }
    }

    private suspend fun normalizeKoreanChunk(input: CharSequence): CharSequence {

        // Normalize endings: 안됔ㅋㅋㅋ -> 안돼ㅋㅋ
        val endingNormalized = KOREAN_TO_NORMALIZE_REGEX.replace(input) {
            processNormalizationCandidate(it).toString()
        }

        // Normalize repeating chars: ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ -> ㅋㅋㅋ
        val exclamationNormalized = REPEATING_CHAR_REGEX.replace(endingNormalized) {
            Matcher.quoteReplacement(it.groupValues[0].take(3))
        }

        // Normalize repeating 2 chars: 훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍 -> 훌쩍훌쩍
        val repeatingNormalized2 = REPEATING_2CHAR_REGEX.replace(exclamationNormalized) {
            Matcher.quoteReplacement(it.groupValues[0].take(4))
        }

        // Normalize repeating 3 chars: 사브작사브작사브작사브작 -> 사브작사브작
        val repeatingNormalized3 = REPEATING_3CHAR_REGEX.replace(repeatingNormalized2) {
            Matcher.quoteReplacement(it.groupValues[0].take(6))
        }

        // Coda normalization (명사 + ㄴ 첨가 정규화): 소린가 -> 소리인가
        val codaNNormalized = normalizeCodaN(repeatingNormalized3)

        // Typo correction: 하겟다 -> 하겠다
        val typoCorrected = correctTypo(codaNNormalized)

        // Spaces, tabs, new lines are replaced with a single space.
        return WHITESPACE_REGEX.replace(typoCorrected) { " " }
    }

    fun correctTypo(chunk: CharSequence): CharSequence {

        var output = chunk

        KoreanDictionaryProvider.typoDictionaryByLength.entries
            .forEach { (wordLen: Int, typoMap: Map<String, String>) ->
                output.sliding(wordLen).forEach { slice ->
                    if (typoMap.containsKey(slice)) {
                        log.debug { "Typo check: $slice -> ${typoMap[slice]}" }
                        output =
                            output.toString().replace(slice.toString(), typoMap[slice].toString(), ignoreCase = true)
                    }
                }
            }

        return output
    }

    suspend fun normalizeCodaN(chunk: CharSequence): CharSequence {
        if (chunk.length < 2)
            return chunk

        val lastTwo = chunk.subSequence(chunk.length - 2, chunk.length)
        val last = chunk[chunk.lastIndex]
        val lastTwoHead = lastTwo[0]

        fun isExceptional(): Boolean =
            koreanContains(Noun, chunk) ||
                koreanContains(Conjunction, chunk) ||
                koreanContains(Adverb, chunk) ||
                koreanContains(Noun, lastTwo) ||
                lastTwoHead < '가' ||
                lastTwoHead > '힣' ||
                CODA_N_EXCPETION.contains(lastTwoHead)

        if (isExceptional()) {
            return chunk
        }

        val tokens = KoreanTokenizer.tokenize(chunk)
        if (tokens.isNotEmpty() && KoreanPosx.Predicates.contains(tokens.first().pos)) {
            return chunk
        }

        val hc = decomposeHangul(lastTwoHead)
        val newHead = StringBuilder()
            .append(chunk.subSequence(0, chunk.length - 2))
            .append(composeHangul(hc.onset, hc.vowel))

        val needNewHead = hc.coda == 'ㄴ' &&
            last in CODA_N_LAST_CHAR &&
            koreanContains(Noun, newHead)
        return if (needNewHead) {
            newHead.append("인").append(last).toString()
        } else {
            chunk
        }
    }

    private fun processNormalizationCandidate(m: kotlin.text.MatchResult): CharSequence {
        val chunk = m.groupValues[1]
        val toNormalize = m.groupValues[2]

        val isNormalized = koreanContains(Noun, chunk) ||
            koreanContains(Eomi, chunk.takeLast(1)) ||
            koreanContains(Eomi, chunk.takeLast(2))

        val normalizedChunk = if (isNormalized) chunk else normalizeEmotionAttachedChunk(chunk, toNormalize)

        return normalizedChunk.toString() + toNormalize
    }

    private fun normalizeEmotionAttachedChunk(s: CharSequence, toNormalize: CharSequence): CharSequence {

        val init = s.take(s.length - 1)
        val secondToLastDecomposed: HangulChar? =
            if (init.isNotEmpty()) {
                val hc = decomposeHangul(init.last())
                if (hc.codaIsEmpty) hc else null
            } else {
                null
            }

        val hc = decomposeHangul(s.last())

        fun hasSecondToLastDecomposed(): Boolean =
            hc.codaIsEmpty &&
                secondToLastDecomposed != null &&
                hc.vowel == toNormalize[0] &&
                Hangul.CODA_MAP.containsKey(hc.onset)

        if (hc.coda in charArrayOf('ㅋ', 'ㅎ')) {
            return buildString {
                append(init)
                append(composeHangul(hc.onset, hc.vowel))
            }
        } else if (hasSecondToLastDecomposed()) {
            val shc = secondToLastDecomposed!!
            return buildString {
                append(init.subSequence(0, init.length - 1))
                append(composeHangul(shc.onset, shc.vowel, hc.onset))
            }
        }
        return s
    }
}
