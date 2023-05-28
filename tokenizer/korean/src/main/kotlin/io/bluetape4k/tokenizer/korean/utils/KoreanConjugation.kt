package io.bluetape4k.tokenizer.korean.utils

import io.bluetape4k.collections.eclipse.emptyFastList
import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.collections.eclipse.unifiedSetOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.tokenizer.korean.utils.Hangul.composeHangul
import io.bluetape4k.tokenizer.korean.utils.Hangul.decomposeHangul
import io.bluetape4k.tokenizer.korean.utils.Hangul.hasCoda
import java.io.Serializable

/**
 * Expands Korean verbs and adjectives to all possible conjugation forms.
 */
object KoreanConjugation: KLogging(), Serializable {

    // ㅋ, ㅎ for 잨ㅋㅋㅋㅋ 잔댛ㅎㅎㅎㅎ
    private val CODAS_COMMON = charArrayOf('ㅂ', 'ㅆ', 'ㄹ', 'ㄴ', 'ㅁ')

    // 파랗다 -> 파래, 파램, 파랠, 파랬
    private val CODAS_FOR_CONTRACTION = charArrayOf('ㅆ', 'ㄹ', 'ㅁ')
    private val CODAS_NO_PAST = charArrayOf('ㅂ', 'ㄹ', 'ㄴ', 'ㅁ')

    private val CODAS_SLANG_CONSONANT = charArrayOf('ㅋ', 'ㅎ')
    private val CODAS_SLANG_VOWEL = charArrayOf('ㅜ', 'ㅠ')

    private val PRE_EOMI_COMMON = "게겠고구기긴길네다더던도든면자잖재져죠지진질".toCharArray()
    private val PRE_EOMI_1_1 = "야서써도준".toCharArray()
    private val PRE_EOMI_1_2 = "어었".toCharArray()
    private val PRE_EOMI_1_3 = "아았".toCharArray()
    private val PRE_EOMI_1_4 = "워웠".toCharArray()
    private val PRE_EOMI_1_5 = "여였".toCharArray()

    private val PRE_EOMI_2 = "노느니냐".toCharArray()
    private val PRE_EOMI_3 = "러려며".toCharArray()
    private val PRE_EOMI_4 = "으".toCharArray()
    private val PRE_EOMI_5 = "은".toCharArray()
    private val PRE_EOMI_6 = "는".toCharArray()
    private val PRE_EOMI_7 = "운".toCharArray()

    // 존대어
    private val PRE_EOMI_RESPECT = "세시실신셔습셨십".toCharArray()

    private val PRE_EOMI_VOWEL = PRE_EOMI_COMMON + PRE_EOMI_2 + PRE_EOMI_3 + PRE_EOMI_RESPECT

    private fun addPreEomi(lastChar: Char, charsToAdd: CharArray): List<String> {
        return charsToAdd.map { lastChar + it.toString() }.toFastList()
    }

    fun conjugatePredicatesToCharArraySet(words: Set<String>, isAdjective: Boolean = false): CharArraySet {
        val newSet = KoreanDictionaryProvider.newCharArraySet()
        newSet.addAll(conjugatePredicated(words, isAdjective))
        return newSet
    }

    private val PRE_EOMI_하다 by lazy { PRE_EOMI_COMMON + PRE_EOMI_2 + PRE_EOMI_6 + PRE_EOMI_RESPECT }
    private val PRE_EOMI_VOWEL_하다 by lazy { PRE_EOMI_VOWEL + PRE_EOMI_1_5 + PRE_EOMI_6 }

    private val adjective_하다_Set = unifiedSetOf("합", "해", "히", "하")
    private val adjective_하다_Set2 = unifiedSetOf("합", "해")

    /**
     * Cases without codas
     * 하다, special case
     */
    private fun expandChar_하다(lastChar: Char, isAdjective: Boolean): List<String> {
        val endings = if (isAdjective) adjective_하다_Set else adjective_하다_Set2
        val preEomi1 = addPreEomi(lastChar, PRE_EOMI_하다)
        val preEomi2 = CODAS_COMMON.map {
            when (it) {
                'ㅆ'  -> composeHangul('ㅎ', 'ㅐ', it).toString()
                else -> composeHangul('ㅎ', 'ㅏ', it).toString()
            }
        }
        val preEomi3 = addPreEomi('하', PRE_EOMI_VOWEL_하다)
        val preEomi4 = addPreEomi('해', PRE_EOMI_1_1)

        return preEomi1 + preEomi2 + preEomi3 + preEomi4 + endings
    }

    private val PRE_EOMI_쏘다 by lazy { PRE_EOMI_VOWEL + PRE_EOMI_2 + PRE_EOMI_1_3 + PRE_EOMI_6 }

    /**
     * 쏘다
     */
    private fun expandChar_쏘다(lastChar: Char, onset: Char): List<String> {
        return addPreEomi(lastChar, PRE_EOMI_쏘다) +
            CODAS_NO_PAST.map { composeHangul(onset, 'ㅗ', it).toString() }.toFastList() +
            fastListOf(
                composeHangul(onset, 'ㅘ').toString(),
                composeHangul(onset, 'ㅘ', 'ㅆ').toString(),
                lastChar.toString()
            )
    }

    private val PRE_EOMI_겨누다 by lazy { PRE_EOMI_VOWEL + PRE_EOMI_1_2 + PRE_EOMI_2 + PRE_EOMI_6 }

    /**
     * 맞추다, 겨누다, 재우다
     */
    private fun expandChar_겨누다(lastChar: Char, onset: Char): List<String> {
        return addPreEomi(lastChar, PRE_EOMI_겨누다) +
            CODAS_NO_PAST.map { composeHangul(onset, 'ㅜ', it).toString() }.toFastList() +
            fastListOf(
                composeHangul(onset, 'ㅝ').toString(),
                composeHangul(onset, 'ㅝ', 'ㅆ').toString(),
                lastChar.toString()
            )
    }

    private val PRE_EOMI_UNION_2_6 by lazy { PRE_EOMI_2 + PRE_EOMI_6 }

    private fun expandChar_치르다(lastChar: Char, onset: Char): List<String> {
        return addPreEomi(lastChar, PRE_EOMI_2 + PRE_EOMI_6) +
            CODAS_NO_PAST.map { composeHangul(onset, 'ㅡ', it).toString() }.toFastList() +
            fastListOf(
                composeHangul(onset, 'ㅝ').toString(),
                composeHangul(onset, 'ㅓ').toString(),
                composeHangul(onset, 'ㅏ').toString(),
                composeHangul(onset, 'ㅝ', 'ㅆ').toString(),
                composeHangul(onset, 'ㅓ', 'ㅆ').toString(),
                composeHangul(onset, 'ㅏ', 'ㅆ').toString(),
                lastChar.toString()
            )
    }

    private fun expandChar_사귀다(lastChar: Char): List<String> {
        return addPreEomi(lastChar, PRE_EOMI_UNION_2_6) +
            CODAS_NO_PAST.map { composeHangul('ㄱ', 'ㅟ', it).toString() }.toFastList() +
            fastListOf(
                composeHangul('ㄱ', 'ㅕ', ' ').toString(),
                composeHangul('ㄱ', 'ㅕ', 'ㅆ').toString(),
                lastChar.toString()
            )
    }

    private fun expandChar_쥐다(lastChar: Char, onset: Char): List<String> {
        return CODAS_NO_PAST.map { composeHangul(onset, 'ㅟ', it).toString() }.toFastList() +
            addPreEomi(lastChar, PRE_EOMI_UNION_2_6) +
            fastListOf(lastChar.toString())

    }

    /**
     * 마시다, 엎드리다, 치다, 이다, 아니다
     */
    private fun expandChar_마시다(lastChar: Char, onset: Char): List<String> {
        return CODAS_NO_PAST.map { composeHangul(onset, 'ㅣ', it).toString() }.toFastList() +
            addPreEomi(lastChar, PRE_EOMI_1_2 + PRE_EOMI_UNION_2_6) +
            fastListOf(
                composeHangul(onset, 'ㅣ', 'ㅂ') + "니",
                composeHangul(onset, 'ㅕ').toString(),
                composeHangul(onset, 'ㅕ', 'ㅆ').toString(),
                lastChar.toString()
            )
    }

    /**
     * 꿰다, 꾀다
     */
    private fun expandChar_꿰다(lastChar: Char, onset: Char, vowel: Char): List<String> {
        return addPreEomi(lastChar, PRE_EOMI_UNION_2_6) +
            CODAS_COMMON.map { composeHangul(onset, vowel, it).toString() }.toFastList() +
            fastListOf(lastChar.toString())
    }

    private val PRE_EOMI_물러서다 by lazy { PRE_EOMI_VOWEL + PRE_EOMI_1_1 + PRE_EOMI_2 + PRE_EOMI_6 }

    /**
     * 나머지 받침없는 서술어 (둘러서다, 켜다, 세다, 캐다, 차다)
     */
    private fun expandChar_물러서다(lastChar: Char, onset: Char, vowel: Char): List<String> {
        return CODAS_COMMON.map { composeHangul(onset, vowel, it).toString() }.toFastList() +
            addPreEomi(lastChar, PRE_EOMI_물러서다) +
            fastListOf(lastChar.toString())
    }

    private val PRE_EOMI_만들다 by lazy { PRE_EOMI_1_2 + PRE_EOMI_3 }
    private val PRE_EOMI_만들다_2 by lazy { PRE_EOMI_2 + PRE_EOMI_6 + PRE_EOMI_RESPECT }

    /**
     * Cases with codas : 만들다, 알다, 풀다
     */
    private fun expandChar_만들다(lastChar: Char, onset: Char, vowel: Char): List<String> {
        return addPreEomi(lastChar, PRE_EOMI_만들다) +
            addPreEomi(composeHangul(onset, vowel), PRE_EOMI_만들다_2).toFastList() +
            fastListOf(
                composeHangul(onset, vowel, 'ㄻ').toString(),
                composeHangul(onset, vowel, 'ㄴ').toString(),
                lastChar.toString()
            )
    }

    private val PRE_EOMI_UNION_4_5 by lazy { PRE_EOMI_4 + PRE_EOMI_5 }

    private fun expandChar_낫다(lastChar: Char, onset: Char): List<String> {
        return addPreEomi(lastChar, PRE_EOMI_UNION_2_6) +
            addPreEomi(composeHangul(onset, 'ㅏ'), PRE_EOMI_UNION_4_5).toFastList() +
            fastListOf(lastChar.toString())
    }

    private val PRE_EOMI_붇다 by lazy { PRE_EOMI_1_2 + PRE_EOMI_1_4 + PRE_EOMI_4 + PRE_EOMI_5 }

    private fun expandChar_붇다(lastChar: Char, onset: Char): List<String> {
        return addPreEomi(lastChar, PRE_EOMI_UNION_2_6) +
            addPreEomi(composeHangul(onset, 'ㅜ'), PRE_EOMI_붇다).toFastList() +
            fastListOf(
                composeHangul(onset, 'ㅜ', 'ㄹ').toString(),
                lastChar.toString()
            )
    }

    private val PRE_EOMI_눕다 by lazy { PRE_EOMI_1_4 + PRE_EOMI_4 + PRE_EOMI_5 }

    private fun expandChar_눕다(lastChar: Char, onset: Char): List<String> {
        return addPreEomi(lastChar, PRE_EOMI_UNION_2_6) +
            addPreEomi(composeHangul(onset, 'ㅜ'), PRE_EOMI_눕다) +
            fastListOf(lastChar.toString())
    }

    private val PRE_EOMI_UNION_1_4_7 by lazy { PRE_EOMI_1_4 + PRE_EOMI_7 }

    /**
     *  간지럽다, 갑작스럽다 -> 갑작스런
     */
    private fun expandChar_간지럽다(lastChar: Char, onset: Char): List<String> {
        return addPreEomi(composeHangul(onset, 'ㅓ'), PRE_EOMI_UNION_1_4_7) +
            fastListOf(
                composeHangul(onset, 'ㅓ').toString(),
                composeHangul(onset, 'ㅓ', 'ㄴ').toString(),
                lastChar.toString()
            )
    }

    private fun expandChar_아름답다(lastChar: Char, onset: Char, vowel: Char): List<String> {
        return addPreEomi(composeHangul(onset, vowel), PRE_EOMI_UNION_1_4_7) +
            fastListOf(
                composeHangul(onset, vowel).toString(),
                lastChar.toString()
            )
    }

    private fun expandChar_놓다(lastChar: Char, onset: Char): List<String> {
        return addPreEomi(lastChar, PRE_EOMI_UNION_2_6) +
            CODAS_COMMON.map { composeHangul(onset, 'ㅗ', it).toString() }.toFastList() +
            fastListOf(
                composeHangul(onset, 'ㅘ').toString(),
                composeHangul(onset, 'ㅗ').toString(),
                lastChar.toString()
            )
    }

    /**
     * 파랗다, 퍼렇다, 어떻다
     */
    private fun expandChar_파랗다(lastChar: Char, onset: Char, vowel: Char): List<String> {
        return CODAS_COMMON.map { composeHangul(onset, vowel, it).toString() }.toFastList() +
            CODAS_FOR_CONTRACTION.map { composeHangul(onset, 'ㅐ', it).toString() }.toFastList() +
            fastListOf(
                composeHangul(onset, 'ㅐ').toString(),
                composeHangul(onset, vowel).toString(),
                lastChar.toString()
            )
    }

    private val PRE_EOMI_있다 by lazy {
        PRE_EOMI_COMMON + PRE_EOMI_1_2 + PRE_EOMI_1_3 + PRE_EOMI_2 + PRE_EOMI_4 + PRE_EOMI_5 + PRE_EOMI_6
    }
    private val PRE_EOMI_밝다 by lazy {
        PRE_EOMI_COMMON + PRE_EOMI_1_2 + PRE_EOMI_1_3 + PRE_EOMI_2 + PRE_EOMI_4 + PRE_EOMI_5
    }

    private val EDGE_CASE = hashSetOf("아니", "입", "입니", "나는")

    private val VOWEL_뀌다 = hashSetOf('ㅞ', 'ㅚ', 'ㅙ')

    fun conjugatePredicated(words: Set<String>, isAdjective: Boolean): Set<String> {

        val expanded: Set<String> = words.flatMap { word ->
            val init = word.substring(0, word.length - 1)
            val lastChar = word.last()
            val lastCharString = lastChar.toString()
            val (onset, vowel, coda) = decomposeHangul(lastChar)

            val expandedList: List<String> =
                if (onset == 'ㅎ' && vowel == 'ㅏ' && coda == ' ') {
                    expandChar_하다(lastChar, isAdjective)
                } else if (vowel == 'ㅗ' && coda == ' ') {
                    expandChar_쏘다(lastChar, onset)
                } else if (vowel == 'ㅜ' && coda == ' ') {
                    // 맞추다, 겨누다, 재우다
                    expandChar_겨누다(lastChar, onset)
                } else if (vowel == 'ㅡ' && coda == ' ') {
                    //  치르다, 구르다, 글르다, 뜨다, 모으다, 고르다, 골르다
                    expandChar_치르다(lastChar, onset)
                } else if (onset == 'ㄱ' && vowel == 'ㅟ' && coda == ' ') {
                    expandChar_사귀다(lastChar)
                } else if (vowel == 'ㅟ' && coda == ' ') {
                    expandChar_쥐다(lastChar, onset)
                } else if (vowel == 'ㅣ' && coda == ' ') {
                    expandChar_마시다(lastChar, onset)
                } else if (vowel in `VOWEL_뀌다` && coda == ' ') {
                    expandChar_꿰다(lastChar, onset, vowel)
                } else if (coda == ' ') {
                    // 나머지 받침없는 서술어 (둘러서다, 켜다, 세다, 캐다, 차다)
                    expandChar_물러서다(lastChar, onset, vowel)
                } else if (coda == 'ㄹ' && ((onset == 'ㅁ' && vowel == 'ㅓ') || vowel == 'ㅡ' || vowel == 'ㅏ' || vowel == 'ㅜ')) {
                    // 만들다, 알다, 풀다
                    expandChar_만들다(lastChar, onset, vowel)
                } else if (vowel == 'ㅏ' && coda == 'ㅅ') {
                    // 낫다, 빼앗다
                    expandChar_낫다(lastChar, onset)
                } else if (onset == 'ㅁ' && vowel == 'ㅜ' && coda == 'ㄷ') {
                    // 묻다
                    addPreEomi(lastChar, PRE_EOMI_UNION_2_6) +
                        fastListOf(composeHangul('ㅁ', 'ㅜ', 'ㄹ').toString(), lastCharString)
                } else if (vowel == 'ㅜ' && coda == 'ㄷ') {
                    expandChar_붇다(lastChar, onset)
                } else if (vowel == 'ㅜ' && coda == 'ㅂ') {
                    expandChar_눕다(lastChar, onset)
                } else if (vowel == 'ㅓ' && coda == 'ㅂ' && isAdjective) {
                    expandChar_간지럽다(lastChar, onset)
                } else if (coda == 'ㅂ' && isAdjective) {
                    expandChar_아름답다(lastChar, onset, vowel)
                } else if (vowel == 'ㅗ' && coda == 'ㅎ') {
                    expandChar_놓다(lastChar, onset)
                } else if (coda == 'ㅎ' && isAdjective) {
                    expandChar_파랗다(lastChar, onset, vowel)
                } else if (word.length == 1 || (isAdjective && coda == 'ㅆ')) {
                    // 1 char with coda adjective, 있다, 컸다
                    addPreEomi(lastChar, PRE_EOMI_있다) + fastListOf(lastCharString)
                } else if (word.length == 1 && isAdjective) {
                    // 1 char with coda adjective, 밝다
                    addPreEomi(lastChar, PRE_EOMI_밝다) + fastListOf(lastCharString)
                } else {
                    // 부여잡다, 얻어맞다, 얻어먹다
                    fastListOf(lastCharString)
                }

            // -르 불규칙 (고르다 -> 골르다)
            val irregularExpression = if (lastChar == '르' && !hasCoda(init.last())) {
                val lastInitCharDecomposed = decomposeHangul(init.last())
                val newInit = init.substring(0, init.length - 1) + composeHangul(
                    lastInitCharDecomposed.onset,
                    lastInitCharDecomposed.vowel,
                    'ㄹ'
                )
                val o = onset
                val conjugation = addPreEomi(lastChar, PRE_EOMI_UNION_2_6) +
                    CODAS_NO_PAST.map { composeHangul(o, 'ㅡ', it).toString() }.toFastList() +
                    fastListOf(
                        composeHangul(o, 'ㅝ').toString(),
                        composeHangul(o, 'ㅓ').toString(),
                        composeHangul(o, 'ㅏ').toString(),
                        composeHangul(o, 'ㅝ', 'ㅆ').toString(),
                        composeHangul(o, 'ㅓ', 'ㅆ').toString(),
                        composeHangul(o, 'ㅏ', 'ㅆ').toString(),
                        lastCharString
                    )

                conjugation.map { newInit + it }.toFastList()
            } else {
                emptyFastList()
            }

            expandedList.map { init + it } + irregularExpression
        }.toUnifiedSet()

        // Edge cases: these more likely to be a conjugation of an adjective than a verb
        return if (isAdjective) expanded else expanded - EDGE_CASE
    }
}
