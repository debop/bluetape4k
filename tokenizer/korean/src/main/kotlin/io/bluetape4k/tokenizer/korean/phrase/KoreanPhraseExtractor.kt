package io.bluetape4k.tokenizer.korean.phrase

import io.bluetape4k.logging.KLogging
import io.bluetape4k.tokenizer.korean.init
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanToken
import io.bluetape4k.tokenizer.korean.utils.Hangul
import io.bluetape4k.tokenizer.korean.utils.KoreanDictionaryProvider
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adjective
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Alpha
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.CashTag
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Hashtag
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Josa
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Noun
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Number
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.ProperNoun
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Space
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Suffix
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Verb
import io.bluetape4k.tokenizer.korean.utils.KoreanPosTrie
import io.bluetape4k.tokenizer.korean.utils.KoreanPosx
import io.bluetape4k.tokenizer.korean.utils.KoreanPosx.SelfNode
import java.io.Serializable


typealias KoreanPhraseChunk = List<KoreanPhrase>

/**
 * KoreanPhraseExtractor extracts suitable phrases for trending topics.
 *
 * 1. Collapse sequence of POSes to phrase candidates (초 + 거대 + 기업 + 의 -> 초거대기업 + 의)
 * 2. Find suitable phrases
 */
object KoreanPhraseExtractor: KLogging(), Serializable {
    const val MinCharsPerPhraseChunkWithoutSpaces = 2
    const val MinPhrasesPerPhraseChunk = 3

    const val MaxCharsPerPhraseChunkWithoutSpaces = 30
    const val MaxPhrasesPerPhraseChunk = 8

    @JvmField
    val ModifyingPredicateEndings = hashSetOf('ㄹ', 'ㄴ')

    @JvmField
    val ModifyingPredicateExceptions = hashSetOf('만')

    @JvmField
    val PhraseTokens = hashSetOf(Noun, ProperNoun, Space)

    @JvmField
    val ConjunctionJosa = hashSetOf("와", "과", "의")

    @JvmField
    val PhraseHeadPoses = hashSetOf(Adjective, Noun, ProperNoun, Alpha, Number)

    @JvmField
    val PhraseTailPoses = hashSetOf(Noun, ProperNoun, Alpha, Number)

    /**
     * 0 for optional, 1 for required
     * * for optional repeatable, + for required repeatable
     *
     * Substantive: 체언 (초거대기업의)
     * Predicate: 용언 (하였었습니다, 개예뻤었다)
     * Modifier: 수식언 (모르는 할수도있는 보이기도하는 예뻐 예쁜 완전 레알 초인간적인 잘 잘한)
     * Standalone: 독립언
     * Functional: 관계언 (조사)
     *
     * N Noun: 명사 (Nouns, Pronouns, Company Names, Proper Noun, Person Names, Numerals, Standalone, Dependent)
     * V Verb: 동사 (하, 먹, 자, 차)
     * J Adjective: 형용사 (예쁘다, 크다, 작다)
     * A Adverb: 부사 (잘, 매우, 빨리, 반드시, 과연)
     * D Determiner: 관형사 (새, 헌, 참, 첫, 이, 그, 저)
     * E Exclamation: 감탄사 (헐, ㅋㅋㅋ, 어머나, 얼씨구)
     *
     * C Conjunction: 접속사
     *
     * j SubstantiveJosa: 조사 (의, 에, 에서)
     * l AdverbialJosa: 부사격 조사 (~인, ~의, ~일)
     * e Eomi: 어말어미 (다, 요, 여, 하댘ㅋㅋ)
     * r PreEomi: 선어말어미 (었)
     *
     * p NounPrefix: 접두사 ('초'대박)
     * v VerbPrefix: 동사 접두어 ('쳐'먹어)
     * s Suffix: 접미사 (~적)
     *
     * a Alpha,
     * n Number
     * o Others
     */
    private val COLLAPSING_RULES = hashMapOf(
        "D0m*N1s0" to Noun, // Substantive
        "n*a+n*" to Noun,
        "n+" to Noun,
        /* Predicate 초기뻐하다, 와주세요, 초기뻤었고, 추첨하다, 구경하기힘들다, 기뻐하는, 기쁜, 추첨해서, 좋아하다, 걸려있을 */
        "v*V1r*e0" to Verb,
        "v*J1r*e0" to Adjective
    )

    private val CollapseTrie by lazy { KoreanPosx.getTrie(COLLAPSING_RULES) }


    private fun trimPhraseChunk(phrases: KoreanPhraseChunk): KoreanPhraseChunk {
        fun trimNonNouns() = phrases
            .dropWhile { !PhraseHeadPoses.contains(it.pos) }
            .dropLastWhile { !PhraseTailPoses.contains(it.pos) }

        fun trimSpacesFromPhrase(phrasesToTrim: Collection<KoreanPhrase>): List<KoreanPhrase> {
            return phrasesToTrim.mapIndexed { i, phrase ->
                when {
                    phrasesToTrim.size == 1 -> KoreanPhrase(phrase.tokens
                        .dropWhile { it.pos == Space }
                        .dropLastWhile { it.pos == Space },
                        phrase.pos
                    )

                    i == 0 -> KoreanPhrase(phrase.tokens.dropWhile { it.pos == Space }, phrase.pos)
                    i == phrasesToTrim.size - 1 -> {
                        val tokens = phrase.tokens.dropLastWhile { it.pos == Space }
                        KoreanPhrase(tokens, phrase.pos)
                    }

                    else -> phrase
                }
            }
        }

        return trimSpacesFromPhrase(trimNonNouns())
    }

    private fun trimPhrase(phrase: KoreanPhrase): KoreanPhrase =
        KoreanPhrase(phrase.tokens
            .dropWhile { it.pos == Space }
            .dropLastWhile { it.pos == Space },
            phrase.pos
        )

    private fun isProperPhraseChunk(phraseChunk: KoreanPhraseChunk): Boolean {
        fun notEndingInNonPhrasesSuffix(): Boolean {
            val lastToken = phraseChunk.last().tokens.last()
            return !(lastToken.pos == Suffix && lastToken.text == "적")
        }

        fun isRightLength(): Boolean {
            val phraseChunkWithoutSpaces = phraseChunk.filter { it.pos != Space }

            fun checkMaxLength(): Boolean {
                return phraseChunkWithoutSpaces.size <= MaxPhrasesPerPhraseChunk &&
                    phraseChunkWithoutSpaces.map { it.length }.sum() <= MaxCharsPerPhraseChunkWithoutSpaces
            }

            fun checkMinLength(): Boolean {
                return phraseChunkWithoutSpaces.size >= MinPhrasesPerPhraseChunk ||
                    (phraseChunkWithoutSpaces.size <= MinPhrasesPerPhraseChunk &&
                        phraseChunkWithoutSpaces.map { it.length }.sum() >= MinCharsPerPhraseChunkWithoutSpaces)
            }

            fun checkMinLengthPerToken(): Boolean {
                return phraseChunkWithoutSpaces.any { it.length > 1 }
            }

            return checkMaxLength() && checkMinLength() && checkMinLengthPerToken()
        }

        return isRightLength() && notEndingInNonPhrasesSuffix()
    }

    fun collapsePos(tokens: List<KoreanToken>): List<KoreanPhrase> {

        fun getTries(token: KoreanToken, trie: List<KoreanPosTrie?>): Pair<KoreanPosTrie?, List<KoreanPosTrie?>> {
            val curTrie = trie.firstOrNull { it != null && it.curPos == token.pos }
            val nextTrie = curTrie?.nextTrie?.map { if (it == SelfNode) curTrie else it } ?: emptyList()
            return Pair(curTrie, nextTrie)
        }

        fun getInit(phraseBuffer: PhraseBuffer): List<KoreanPhrase> =
            if (phraseBuffer.phrases.isEmpty()) emptyList() else phraseBuffer.phrases.init()

        // HINT: Change fold to forEach for repeatly creation objects
        //    return tokens.fold(PhraseBuffer(emptyList(), CollapseTrie, null)) { output, token ->
        //      if (output.curTrie.any { it != null && it.curPos == token.pos }) {
        //        // Extend the current phrase
        //        val (ct, nt) = getTries(token, output.curTrie)
        //
        //        if (output.phrases.isEmpty() || output.curTrie == CollapseTrie) {
        //          PhraseBuffer(output.phrases + KoreanPhrase(listOf(token), ct?.ending ?: Noun),
        //                       nt,
        //                       ct?.ending)
        //        } else {
        //          PhraseBuffer(getInit(output) + KoreanPhrase(output.phrases.last().tokens + token, ct?.ending ?: Noun),
        //                       nt,
        //                       ct?.ending)
        //        }
        //      } else if (CollapseTrie.any { it.curPos == token.pos }) {
        //        // Start a new phrase
        //        val (ct, nt) = getTries(token, CollapseTrie)
        //
        //        PhraseBuffer(output.phrases + KoreanPhrase(listOf(token), ct?.ending ?: Noun),
        //                     nt,
        //                     ct?.ending)
        //      } else {
        //        // Add a single word
        //        PhraseBuffer(output.phrases + KoreanPhrase(listOf(token), token.pos),
        //                     CollapseTrie,
        //                     output.ending)
        //      }
        //    }.phrases

        val phrases = arrayListOf<KoreanPhrase>()
        var curTrie: List<KoreanPosTrie?> = CollapseTrie

        tokens.forEach { token ->
            when {
                curTrie.any { it != null && it.curPos == token.pos } -> {
                    // Extend the current phase
                    val (ct, nt) = getTries(token, curTrie)

                    if (phrases.isEmpty() || curTrie == CollapseTrie) {
                        phrases.add(KoreanPhrase(arrayListOf(token), ct?.ending ?: Noun))
                    } else {
                        val newPhrase = KoreanPhrase(phrases.last().tokens + token, ct?.ending ?: Noun)
                        if (phrases.isEmpty()) {
                            phrases.add(newPhrase)
                        } else {
                            phrases[phrases.lastIndex] = newPhrase
                        }
                    }
                    curTrie = nt
                }

                CollapseTrie.any { it.curPos == token.pos } -> {
                    // Start a new phrase
                    val (ct, nt) = getTries(token, CollapseTrie)
                    phrases.add(KoreanPhrase(arrayListOf(token), ct?.ending ?: Noun))
                    curTrie = nt
                }

                else                                        -> {
                    // Add a single word
                    phrases.add(KoreanPhrase(arrayListOf(token), token.pos))
                    curTrie = CollapseTrie
                }
            }
        }

        return phrases
    }

    private fun distinctPhrases(chunks: List<KoreanPhraseChunk>): List<KoreanPhraseChunk> {

        val phraseChunks = arrayListOf<KoreanPhraseChunk>()
        val buffer = hashSetOf<String>()

        chunks.forEach { chunk ->
            val phraseText = chunk.map { it.tokens.map { it.text }.joinToString("") }.joinToString("")
            if (!buffer.contains(phraseText)) {
                phraseChunks.add(0, chunk)
                buffer.add(phraseText)
            }
        }
        return phraseChunks.reversed()
    }

    private fun getCandidatePhraseChunks(
        phrases: KoreanPhraseChunk,
        filterSpam: Boolean = false,
    ): List<KoreanPhraseChunk> {
        fun isNotSpam(phrase: KoreanPhrase): Boolean {
            return !filterSpam ||
                !phrase.tokens.any { KoreanDictionaryProvider.spamNouns.contains(it.text) }
        }

        fun isNonNounPhraseCandidate(phrase: KoreanPhrase): Boolean {
            val trimmed = trimPhrase(phrase)

            // 하는, 할인된, 할인될, exclude: 하지만
            fun isModifyingPredicate(): Boolean {
                val lastChar = trimmed.tokens.last().text.last()
                return (trimmed.pos == Verb || trimmed.pos == Adjective) &&
                    ModifyingPredicateEndings.contains(Hangul.decomposeHangul(lastChar).coda) &&
                    !ModifyingPredicateExceptions.contains(lastChar)
            }

            // 과, 와, 의
            fun isConjunction(): Boolean =
                trimmed.pos == Josa && ConjunctionJosa.contains(trimmed.tokens.last().text)

            fun isAlphaNumeric(): Boolean =
                trimmed.pos == Alpha || trimmed.pos == Number

            return isAlphaNumeric() || isModifyingPredicate() || isConjunction()
        }

        fun collapseNounPhrases(phrases1: KoreanPhraseChunk): KoreanPhraseChunk {

            val output = arrayListOf<KoreanPhrase>()
            val buffer = arrayListOf<KoreanPhrase>()

            phrases1.forEach {
                if (it.pos == Noun || it.pos == ProperNoun) {
                    buffer.add(it)
                } else {
                    val tempPhrase =
                        if (buffer.isNotEmpty()) arrayListOf(KoreanPhrase(buffer.flatMap { it.tokens }), it)
                        else arrayListOf(it)
                    output.addAll(tempPhrase)
                    buffer.clear()
                }
            }

            if (buffer.isNotEmpty()) {
                output.add(KoreanPhrase(buffer.flatMap { it.tokens }))
            }
            return output
        }

        fun collapsePhrases(phrases1: KoreanPhraseChunk): List<KoreanPhraseChunk> {
            fun addPhraseToBuffer(phrase: KoreanPhrase, buffer: List<KoreanPhraseChunk>): List<KoreanPhraseChunk> =
                buffer.map { it + phrase }.toList()

            // NOTE: 현재 이 부분은 변경하면 안됩니다.
            //
            fun newBuffer() = listOf(listOf<KoreanPhrase>())

            val output = arrayListOf<KoreanPhraseChunk>()
            var buffer = newBuffer()

            phrases1.forEach {
                buffer = if (it.pos in PhraseTokens && isNotSpam(it)) {
                    val bufferWithThisPhrase = addPhraseToBuffer(it, buffer)
                    if (it.pos == Noun || it.pos == ProperNoun) {
                        output.addAll(bufferWithThisPhrase)
                    }
                    bufferWithThisPhrase
                } else if (isNonNounPhraseCandidate(it)) {
                    addPhraseToBuffer(it, buffer)
                } else {
                    output.addAll(buffer)
                    newBuffer()
                }
            }

            if (buffer.isNotEmpty()) {
                output.addAll(buffer)
                return output
            }
            return buffer
        }

        fun getSingleTokenNouns(): List<KoreanPhraseChunk> {

            fun isSingle(phrase: KoreanPhrase): Boolean {
                val trimmed = trimPhrase(phrase)

                return phrase.pos in setOf(Noun, ProperNoun) &&
                    isNotSpam(phrase) &&
                    (trimmed.length >= MinCharsPerPhraseChunkWithoutSpaces || trimmed.tokens.size >= MinPhrasesPerPhraseChunk)
            }

            return phrases
                .filter { isSingle(it) }
                .map { arrayListOf(trimPhrase(it)) }
        }

        val nounPhrases = collapseNounPhrases(phrases)
        val phraseCollapsed = collapsePhrases(nounPhrases)

        return distinctPhrases(phraseCollapsed.map(this::trimPhraseChunk) + getSingleTokenNouns())
    }

    /**
     * Find suitable phrases
     *
     * @param tokens A sequence of tokens
     * @param filterSpam true if spam words and slangs to be filtered out
     * @param addHashtags true if #hashtags to be included
     * @return A list of KoreanPhrase
     */
    fun extractPhrases(
        tokens: List<KoreanToken>,
        filterSpam: Boolean = false,
        addHashtags: Boolean = true,
    ): List<KoreanPhrase> {

        val collapsed = collapsePos(tokens)
        val candidates = getCandidatePhraseChunks(collapsed, filterSpam)
        val permutatedCandidates = permutateCandidates(candidates)

        val phrases = permutatedCandidates.map { KoreanPhrase(trimPhraseChunk(it).flatMap { it.tokens }) }

        if (addHashtags) {
            val hashtags = tokens
                .filter { it.pos in listOf(Hashtag, CashTag) }
                .map { KoreanPhrase(listOf(it), it.pos) }

            return phrases + hashtags
        }
        return phrases
    }

    private fun permutateCandidates(candidates: List<KoreanPhraseChunk>): List<KoreanPhraseChunk> =
        distinctPhrases(candidates.filter { isProperPhraseChunk(it) })
}
