package io.bluetape4k.tokenizer.korean.utils

import io.bluetape4k.collections.eclipse.toUnifiedMap
import io.bluetape4k.collections.eclipse.unifiedMapOf
import org.eclipse.collections.impl.map.mutable.UnifiedMap
import java.io.Serializable

object Hangul: Serializable {

    data class HangulChar(val onset: Char, val vowel: Char, val coda: Char) {
        val codaIsEmpty: Boolean get() = coda == ' '
        val hasCoda: Boolean get() = coda != ' '
    }

    data class DoubleCoda(val first: Char, val second: Char)

    private const val HANGUL_BASE: Int = 0xAC00
    private const val ONSET_BASE: Int = 21 * 28
    private const val VOWEL_BASE: Int = 28

    private val ONSET_LIST: CharArray = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
        'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )
    private val VOWEL_LIST: CharArray = charArrayOf(
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ',
        'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ',
        'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ',
        'ㅡ', 'ㅢ', 'ㅣ'
    )
    private val CODA_LIST: CharArray = charArrayOf(
        ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ',
        'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ',
        'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ',
        'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    private val ONSET_MAP: UnifiedMap<Char, Int> = ONSET_LIST.mapIndexed { index, c -> c to index }.toUnifiedMap()
    private val VOWEL_MAP: UnifiedMap<Char, Int> = VOWEL_LIST.mapIndexed { index, c -> c to index }.toUnifiedMap()
    val CODA_MAP: UnifiedMap<Char, Int> = CODA_LIST.mapIndexed { index, c -> c to index }.toUnifiedMap()

    val DOUBLE_CODAS: UnifiedMap<Char, DoubleCoda> = unifiedMapOf(
        'ㄳ' to DoubleCoda('ㄱ', 'ㅅ'),
        'ㄵ' to DoubleCoda('ㄴ', 'ㅈ'),
        'ㄶ' to DoubleCoda('ㄴ', 'ㅎ'),
        'ㄺ' to DoubleCoda('ㄹ', 'ㄱ'),
        'ㄻ' to DoubleCoda('ㄹ', 'ㅁ'),
        'ㄼ' to DoubleCoda('ㄹ', 'ㅂ'),
        'ㄽ' to DoubleCoda('ㄹ', 'ㅅ'),
        'ㄾ' to DoubleCoda('ㄹ', 'ㅌ'),
        'ㄿ' to DoubleCoda('ㄹ', 'ㅍ'),
        'ㅀ' to DoubleCoda('ㄹ', 'ㅎ'),
        'ㅄ' to DoubleCoda('ㅂ', 'ㅅ')
    )

    /**
     *  한글을 초성, 중성, 종성으로 분해합니다.
     *  @param c Korean Character
     *  @return (onset:Char, vowel:Char, coda:Char)
     */
    fun decomposeHangul(c: Char): HangulChar {
        require(!(ONSET_MAP.containsKey(c) || VOWEL_MAP.containsKey(c) || CODA_MAP.containsKey(c))) {
            "Input character is not a valid Korean character"
        }
        val u = (c - HANGUL_BASE).code
        return HangulChar(
            ONSET_LIST[u / ONSET_BASE],
            VOWEL_LIST[(u % ONSET_BASE) / VOWEL_BASE],
            CODA_LIST[u % VOWEL_BASE]
        )
    }

    /**
     * 한글에 종송 (받침) 이 있는지 검사
     */
    fun hasCoda(c: Char): Boolean = (c.code - HANGUL_BASE) % VOWEL_BASE > 0

    /**
     * 초,중,종성의 char 로 한글을 조홥합니다.
     * @param onset 초성
     * @param vowel 중성
     * @param coda 종성
     */
    fun composeHangul(onset: Char, vowel: Char, coda: Char = ' '): Char {
        require(onset != ' ' && vowel != ' ') { "Input characters are not valid" }

        return (HANGUL_BASE +
            ((ONSET_MAP[onset] ?: 0) * ONSET_BASE) +
            ((VOWEL_MAP[vowel] ?: 0) * VOWEL_BASE) +
            (CODA_MAP[coda] ?: 0)).toChar()
    }

    /**
     * [HangulChar] 의 초성, 중성, 종성으로 한글을 조합합니다.
     */
    fun composeHangul(hc: HangulChar): Char = composeHangul(hc.onset, hc.vowel, hc.coda)
}
