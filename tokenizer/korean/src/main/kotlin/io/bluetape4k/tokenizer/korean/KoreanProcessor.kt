package io.bluetape4k.tokenizer.korean

import io.bluetape4k.tokenizer.korean.block.KoreanBlockwordProcessor
import io.bluetape4k.tokenizer.korean.normalizer.KoreanNormalizer
import io.bluetape4k.tokenizer.korean.phrase.KoreanPhrase
import io.bluetape4k.tokenizer.korean.phrase.KoreanPhraseExtractor
import io.bluetape4k.tokenizer.korean.phrase.NounPhraseExtractor
import io.bluetape4k.tokenizer.korean.stemmer.KoreanStemmer
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanDetokenizer
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanSentenceSplitter
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanToken
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanTokenizer
import io.bluetape4k.tokenizer.korean.tokenizer.NounTokenizer
import io.bluetape4k.tokenizer.korean.tokenizer.Sentence
import io.bluetape4k.tokenizer.korean.tokenizer.TokenizerProfile
import io.bluetape4k.tokenizer.korean.utils.KoreanDictionaryProvider
import io.bluetape4k.tokenizer.korean.utils.KoreanPos
import io.bluetape4k.tokenizer.model.BlockwordRequest
import io.bluetape4k.tokenizer.model.BlockwordResponse
import io.bluetape4k.tokenizer.model.Severity
import io.bluetape4k.tokenizer.model.Severity.HIGH
import io.bluetape4k.tokenizer.model.Severity.LOW
import io.bluetape4k.tokenizer.model.Severity.MIDDLE
import io.bluetape4k.tokenizer.utils.CharArraySet
import kotlinx.coroutines.flow.Flow

/**
 * 한글 형태소 분석기
 */
object KoreanProcessor {

    /**
     * Normalize Korean text. Uses KoreanNormalizer.normalize().
     *
     * @param text Input text
     * @return Normalized Korean text
     */
    suspend fun normalize(text: CharSequence): CharSequence {
        return KoreanNormalizer.normalize(text)
    }


    /**
     * Tokenize text (with a custom profile) into a sequence of KoreanTokens,
     * which includes part-of-speech information and whether a token is an out-of-vocabulary term.
     *
     * @param text input text
     * @param profile TokenizerProfile
     * @return A sequence of KoreanTokens.
     */
    suspend fun tokenize(
        text: CharSequence,
        profile: TokenizerProfile = TokenizerProfile.DefaultProfile,
    ): List<KoreanToken> {
        return KoreanTokenizer.tokenize(text, profile)
    }

    // TODO: NounTokenizer 를 쓰세요 (명사위주로 분석)
    suspend fun tokenizeForNoun(
        text: CharSequence,
        profile: TokenizerProfile = TokenizerProfile.DefaultProfile,
    ): List<KoreanToken> {
        return NounTokenizer.tokenize(text, profile)
    }


    /**
     * Tokenize text (with a custom profile) into a sequence of KoreanTokens,
     * which includes part-of-speech information and whether a token is an out-of-vocabulary term,
     * and return top `n` candidates.
     *
     * @param text input text
     * @param n number of top candidates
     * @param profile TokenizerProfile
     * @return A sequence of sequences of KoreanTokens.
     */
    suspend fun tokenizeTopN(
        text: CharSequence,
        n: Int = 1,
        profile: TokenizerProfile = TokenizerProfile.DefaultProfile,
    ): List<List<List<KoreanToken>>> =
        KoreanTokenizer.tokenizeTopN(text, n, profile)

    /**
     * Add user-defined word list to the noun dictionary. Spaced words are not allowed.
     *
     * @param words Sequence of words to add.
     */
    fun addNounsToDictionary(words: List<String>) {
        KoreanDictionaryProvider.addWordsToDictionary(KoreanPos.Noun, words)
    }

    /**
     * Add user-defined word list to the noun dictionary. Spaced words are not allowed.
     *
     * @param words Sequence of words to add.
     */
    fun addNounsToDictionary(vararg words: String) {
        KoreanDictionaryProvider.addWordsToDictionary(KoreanPos.Noun, *words)
    }


    /**
     * 금칙어를 금칙어 Dictionary에 추가합니다.
     *
     * @param words 금칙어에 등록할 단어들
     */
    fun addBlockwords(
        words: List<String>,
        severity: Severity = Severity.DEFAULT,
    ) {
        io.bluetape4k.tokenizer.korean.KoreanProcessor.withBlockwordDictionary(severity) {
            addAll(words)
        }
        // 복합명사의 경우 등록되지 않으면 형태소 분석을 못한다 (예: 분수쑈 -> `분수 + 쑈` 로 분석하면 `분수쑈` 라는 금칙어를 처리할 수 없다)
        io.bluetape4k.tokenizer.korean.KoreanProcessor.addNounsToDictionary(words)
        KoreanDictionaryProvider.properNouns.addAll(words)
    }

    /**
     * 등록된 금칙어를 제외시킵니다
     *
     * @param words
     * @param severity
     */
    @Deprecated("Use removeBlockwords instead", replaceWith = ReplaceWith("removeBlockwords(words, severity)"))
    fun removeBlockword(
        words: List<String>,
        severity: Severity = Severity.DEFAULT,
    ) {
        io.bluetape4k.tokenizer.korean.KoreanProcessor.withBlockwordDictionary(severity) {
            removeAll(words)
        }
    }

    /**
     * 등록된 금칙어를 제외시킵니다
     *
     * @param words
     * @param severity
     */
    fun removeBlockwords(
        words: List<String>,
        severity: Severity = Severity.DEFAULT,
    ) {
        io.bluetape4k.tokenizer.korean.KoreanProcessor.withBlockwordDictionary(severity) {
            removeAll(words)
        }
    }

    /**
     * 등록된 금칙어를 모두 삭제합니다.
     *
     * @param severity
     */
    fun clearBlockwords(severity: Severity = Severity.DEFAULT) {
        io.bluetape4k.tokenizer.korean.KoreanProcessor.withBlockwordDictionary(severity) {
            clear()
        }
    }

    private inline fun withBlockwordDictionary(
        severity: Severity,
        action: CharArraySet.() -> Unit,
    ) {
        when (severity) {
            LOW    -> {
                KoreanDictionaryProvider.blockWords[Severity.LOW]?.action()
                KoreanDictionaryProvider.blockWords[Severity.MIDDLE]?.action()
                KoreanDictionaryProvider.blockWords[Severity.HIGH]?.action()
            }

            MIDDLE -> {
                KoreanDictionaryProvider.blockWords[MIDDLE]?.action()
                KoreanDictionaryProvider.blockWords[HIGH]?.action()
            }

            else   ->
                KoreanDictionaryProvider.blockWords[HIGH]?.action()
        }
    }

    /**
     * Tokenize text into a sequence of token strings. This excludes spaces.
     *
     * @param tokens Korean tokens
     * @return A sequence of token strings.
     */
    fun tokensToStrings(tokens: List<KoreanToken>): List<String> =
        tokens.filterNot { it.pos == KoreanPos.Space }.map { it.text }

    /**
     * Split input text into sentences.
     *
     * @param text input text
     * @return A sequence of sentences.
     */
    fun splitSentences(text: CharSequence): Flow<Sentence> = KoreanSentenceSplitter.split(text)

    /**
     * Extract noun-phrases from Korean text
     *
     * @param tokens         Korean tokens
     * @param filterSpam     true if spam/slang terms to be filtered out (default: false)
     * @param enableHashtags true if #hashtags to be included (default: true)
     * @return A sequence of extracted phrases
     */
    suspend fun extractPhrases(
        tokens: List<KoreanToken>,
        filterSpam: Boolean = false,
        enableHashtags: Boolean = true,
    ): List<KoreanPhrase> {
        return KoreanPhraseExtractor.extractPhrases(tokens, filterSpam, enableHashtags)
    }


    /**
     * Extract noun-phrases from Korean text
     *
     * @param tokens         Korean tokens
     * @return A sequence of extracted phrases
     */
    suspend fun extractPhrasesForNoun(tokens: List<KoreanToken>): List<KoreanPhrase> {
        return NounPhraseExtractor.extractPhrases(tokens)
    }

    /**
     * Removes Ending tokens recovering the root form of predicates
     *
     * @param tokens A sequence of tokens
     * @return A sequence of collapsed Korean tokens
     */
    suspend fun stem(tokens: List<KoreanToken>): List<KoreanToken> {
        return KoreanStemmer.stem(tokens)
    }


    /**
     * Detokenize the input list of words.
     *
     * @param tokens List of words.
     * @return Detokenized string.
     */
    suspend fun detokenize(tokens: Collection<String>): String {
        return KoreanDetokenizer.detokenize(tokens)
    }

    /**
     * 금칙어 (Block words) 를 masking 합니다.
     *
     * 예: 미니미와 니미 -> 미니미와 **        // `니미` 는 속어
     *
     * @param request 금칙어 처리 요청 정보 [BlockwordRequest]
     * @return 금칙어를 처리한 결과 [BlockwordResponse]
     */
    suspend fun maskBlockwords(request: BlockwordRequest): BlockwordResponse {
        return KoreanBlockwordProcessor.maskBlockwords(request)
    }
}
