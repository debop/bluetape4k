package io.bluetape4k.tokenizer.korean.tokenizer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import io.bluetape4k.logging.trace
import io.bluetape4k.tokenizer.korean.stemmer.KoreanStemmer
import io.bluetape4k.tokenizer.korean.utils.KoreanDictionaryProvider.koreanDictionary
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adjective
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adverb
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Conjunction
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Exclamation
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Josa
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Korean
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Noun
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Unknown
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Verb
import io.bluetape4k.tokenizer.korean.utils.KoreanPosx
import io.bluetape4k.tokenizer.korean.utils.KoreanSubstantive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

/**
 * Provides Korean tokenization.
 *
 * Chunk: 어절 - 공백으로 구분되어 있는 단위 (사랑하는사람을)
 * Word: 단어 - 하나의 문장 구성 요소 (사랑하는, 사람을)
 * Token: 토큰 - 형태소와 비슷한 단위이지만 문법적으로 정확하지는 않음 (사랑, 하는, 사람, 을)
 *
 * Whenever there is an updates in the behavior of KoreanParser,
 * the initial cache has to be updated by running tools.CreateInitialCache.
 */
object KoreanTokenizer: KLogging() {

    private const val TOP_N_PER_STATE = 5
    private const val MAX_TRACE_BACK = 8

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
     * m Modifier: 관형사 ('초'대박)
     * v VerbPrefix: 동사 접두어 ('쳐'먹어)
     * s Suffix: 접미사 (~적)
     */
    private val sequenceDefinition = mapOf(
        // Substantive
        "D0m*N1s0j0" to Noun,
        // Predicate 초기뻐하다, 와주세요, 초기뻤었고, 추첨하다, 구경하기힘들다, 기뻐하는, 기쁜, 추첨해서, 좋아하다, 걸려있을
        "v*V1r*e0" to Verb,
        "v*J1r*e0" to Adjective,
        // Modifier 부사
        "A1" to Adverb,
        // Standalone
        "C1" to Conjunction,
        "E+" to Exclamation,
        "j1" to Josa
    )

    private val koreanPosTrie by lazy {
        KoreanPosx.getTrie(sequenceDefinition)
    }

    /**
     * Parse Korean text into a sequence of KoreanTokens with custom parameters
     *
     * @param text Input Korean chunk
     * @return sequence of KoreanTokens
     */
    fun tokenize(
        text: CharSequence,
        profile: TokenizerProfile = TokenizerProfile.DefaultProfile,
    ): List<KoreanToken> {
        val tokenized = tokenizeTopN(text, 1, profile)
            .flatMap { it.firstOrNull() ?: emptyList() }

        return KoreanStemmer.stem(tokenized)
    }

    /**
     * Parse Korean text into a sequence of KoreanTokens with custom parameters
     *
     * @param text Input Korean chunk
     * @param topN number of top candidates
     * @return sequence of KoreanTokens
     */
    fun tokenizeTopN(
        text: CharSequence,
        topN: Int = 1,
        profile: TokenizerProfile = TokenizerProfile.DefaultProfile,
    ): List<List<List<KoreanToken>>> {
        try {
            return KoreanChunker.chunk(text)
                .map {
                    when (it.pos) {
                        Korean -> {
                            // Get the best parse of each chunk
                            runBlocking(Dispatchers.IO) {
                                val parsed = parseKoreanChunk(it, profile, topN)

                                // Collapse sequence of one-char nouns into one unknown noun: (가Noun 회Noun -> 가회Noun*)
                                parsed.map(KoreanSubstantive::collapseNouns).toList()
                            }
                        }

                        else   -> listOf(listOf(it))
                    }
                }
        } catch (e: Exception) {
            log.error(e) { "Error tokenizing a chunk: $text" }
            throw e
        }
    }

    /**
     * Find the best parse using dynamic programming.
     *
     * @param chunk Input chunk. The input has to be entirely. Check for input validity is skipped
     *              for performance optimization. This method is private and is called only by tokenize.
     * @return The best possible parse.
     */
    private suspend fun parseKoreanChunk(
        chunk: KoreanToken,
        profile: TokenizerProfile = TokenizerProfile.DefaultProfile,
        topN: Int = 1,
    ): List<List<KoreanToken>> {
        return findTopCandidates(chunk, profile).take(topN)
    }

    private suspend fun findTopCandidates(
        chunk: KoreanToken,
        profile: TokenizerProfile,
    ): List<List<KoreanToken>> = coroutineScope {
        val directMatch = findDirectMatch(chunk)

        // Buffer for solution
        val candidateParse = listOf(
            CandidateParse(
                parse = ParsedChunk(listOf(), 1, profile),
                curTrie = koreanPosTrie,
                ending = null
            )
        )
        val solutions = hashMapOf(0 to candidateParse)

        // Find N best parses per state
        for (end in 1..chunk.length) {
            for (start in end - 1 downTo maxOf(end - MAX_TRACE_BACK, 0)) {

                val word = chunk.text.slice(start until end)
                // log.trace { "chunk text=${chunk.text}, start=$start, end=$end, word=$word" }

                // Removing unused solutions from solutions hashmap as the chunk is getting processed
                removeUnusedSolutions(start, end, solutions)

                val curSolutions = solutions[start]!!
                // log.trace { "chunk=${chunk.text} word=$word, curSolutions=${curSolutions.joinToString()}" }

                val candidates: List<CandidateParse> = curSolutions.flatMap { solution: CandidateParse ->
                    val possiblePoses: List<PossibleTrie> = solution.ending
                        ?.let {
                            solution.curTrie.map { PossibleTrie(it, 0) } + koreanPosTrie.map { PossibleTrie(it, 1) }
                        }
                        ?: solution.curTrie.map { PossibleTrie(it, 0) }

                    possiblePoses
                        .filter {
                            it.curTrie.curPos == Noun ||
                                    (koreanDictionary[it.curTrie.curPos]?.contains(word.toCharArray()) == true)
                        }
                        .map { t: PossibleTrie ->
                            // log.trace { "word=$word, trie=${t.curTrie}, pos=${t.curTrie.curPos}" }
                            val candidateToAdd =
                                if (t.curTrie.curPos == Noun && koreanDictionary[Noun]?.contains(word.toCharArray()) == false) {
                                    val isWordName: Boolean = KoreanSubstantive.isName(word)
                                    val isKoreanNumber = KoreanSubstantive.isKoreanNumber(word)
                                    val isWordKoreanNameVariation = KoreanSubstantive.isKoreanNameVariation(word)

                                    val unknown = !(isWordName || isKoreanNumber || isWordKoreanNameVariation)
                                    val pos = Noun

                                    ParsedChunk(
                                        listOf(
                                            KoreanToken(
                                                word,
                                                pos,
                                                chunk.offset + start,
                                                word.length,
                                                unknown = unknown
                                            )
                                        ),
                                        t.words,
                                        profile
                                    )
                                } else {
                                    val pos = t.curTrie.curPos ?: Unknown
                                    ParsedChunk(
                                        listOf(KoreanToken(word, pos, chunk.offset + start, word.length)),
                                        t.words,
                                        profile
                                    )
                                }
                            // log.trace { "candidateToAdd=$candidateToAdd" }

                            val nextTrie = t.curTrie.nextTrie
                                ?.map { if (it == KoreanPosx.SelfNode) t.curTrie else it }
                                ?.toList()
                                ?: emptyList()

                            CandidateParse(solution.parse + candidateToAdd, nextTrie, t.curTrie.ending)
                        }
                }

                val currentSolutions = solutions[end] ?: emptyList()

                solutions[end] = (currentSolutions + candidates)
                    .sortedWith(compareBy({ it.parse.score }, { it.parse.posTieBreaker }))
                    .take(TOP_N_PER_STATE)
                    .toList()

                //                solutions[end]?.forEach {
                //                    log.trace { "score=${it.parse.score}, posNodes=${it.parse.posNodes}" }
                //                }
            }
        }

        val topCandidates =
            if (solutions[chunk.length]!!.isEmpty()) {
                listOf(listOf(KoreanToken(chunk.text, Noun, 0, chunk.length, unknown = true)))
            } else {
                solutions[chunk.length]!!.sortedBy { it.parse.score }.map { it.parse.posNodes }.toList()
            }

        (directMatch + topCandidates).distinct()
    }

    private fun removeUnusedSolutions(
        start: Int,
        end: Int,
        solutions: HashMap<Int, List<CandidateParse>>,
    ): HashMap<Int, List<CandidateParse>> {
        // Make sure the solutions hashmap won't have references to unused objects...
        if (end > MAX_TRACE_BACK && start + 1 == end) {
            log.trace { "remove solution. index=${end - MAX_TRACE_BACK - 1}" }
            solutions.remove(end - MAX_TRACE_BACK - 1)
        }
        return solutions
    }

    private fun findDirectMatch(chunk: KoreanToken): List<List<KoreanToken>> {
        log.trace { "Find direct match. chunk=$chunk" }
        return koreanDictionary.entries
            .firstOrNull { (_, dict) ->
                dict.contains(chunk.text)
            }
            ?.let { (pos, _) -> listOf(listOf(chunk.copy(pos = pos))) }
            ?: emptyList()
    }
}
