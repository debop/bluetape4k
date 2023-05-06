package io.bluetape4k.tokenizer.korean

import io.bluetape4k.tokenizer.korean.block.BlockwordProcessor
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
import io.bluetape4k.tokenizer.korean.utils.CharArraySet
import io.bluetape4k.tokenizer.korean.utils.KoreanDictionaryProvider
import io.bluetape4k.tokenizer.korean.utils.KoreanPos
import io.bluetape4k.tokenizer.model.BlockwordRequest
import io.bluetape4k.tokenizer.model.BlockwordResponse
import io.bluetape4k.tokenizer.model.Severity
import io.bluetape4k.tokenizer.model.Severity.HIGH
import io.bluetape4k.tokenizer.model.Severity.LOW
import io.bluetape4k.tokenizer.model.Severity.MIDDLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

/**
 * 한글 형태소 분석기
 */
object KoreanProcessor {

    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Normalize Korean text. Uses KoreanNormalizer.normalize().
     *
     * @param text Input text
     * @return Normalized Korean text
     */
    fun normalize(text: CharSequence): CharSequence = KoreanNormalizer.normalize(text)

    /**
     * Async Normalize Korean text. Uses KoreanNormalizer.normalize().
     *
     * @param text Input text
     * @return Normalized Korean text
     */
    fun normalizeAsync(text: CharSequence): Deferred<CharSequence> {
        return scope.async {
            KoreanNormalizer.normalize(text)
        }
    }

    /**
     * Tokenize text (with a custom profile) into a sequence of KoreanTokens,
     * which includes part-of-speech information and whether a token is an out-of-vocabulary term.
     *
     * @param text input text
     * @param profile TokenizerProfile
     * @return A sequence of KoreanTokens.
     */
    @JvmOverloads
    fun tokenize(
        text: CharSequence,
        profile: TokenizerProfile = TokenizerProfile.DefaultProfile,
    ): List<KoreanToken> {
        return KoreanTokenizer.tokenize(text, profile)
    }

    @JvmOverloads
    fun tokenizeForProduct(
        text: CharSequence,
        profile: TokenizerProfile = TokenizerProfile.DefaultProfile,
    ): List<KoreanToken> {
        return NounTokenizer.tokenize(text, profile)
    }


    /**
     * Tokenize text (with a custom profile) into a sequence of KoreanTokens,
     * which includes part-of-speech information and whether a token is an out-of-vocabulary term.
     *
     * @param text input text
     * @param profile TokenizerProfile
     * @return A sequence of KoreanTokens.
     */
    @JvmOverloads
    fun tokenizeAsync(
        text: CharSequence,
        profile: TokenizerProfile = TokenizerProfile.DefaultProfile,
    ): Deferred<List<KoreanToken>> {
        return scope.async {
            KoreanTokenizer.tokenize(text, profile)
        }
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
    fun tokenizeTopN(
        text: CharSequence,
        n: Int = 1,
        profile: TokenizerProfile = TokenizerProfile.DefaultProfile,
    ): List<List<List<KoreanToken>>> =
        KoreanTokenizer.tokenizeTopN(text, n, profile)

    fun tokenizeTopNAsync(
        text: CharSequence,
        n: Int = 1,
        profile: TokenizerProfile = TokenizerProfile.DefaultProfile,
    ): Deferred<List<List<List<KoreanToken>>>> {
        return scope.async {
            KoreanTokenizer.tokenizeTopN(text, n, profile)
        }
    }


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
    fun addBlockwords(words: List<String>, severity: Severity = Severity.DEFAULT) {
        withBlockwordDictionary(severity) {
            addAll(words)
        }
        // 복합명사의 경우 등록되지 않으면 형태소 분석을 못한다 (예: 분수쑈 -> `분수 + 쑈` 로 분석하면 `분수쑈` 라는 금칙어를 처리할 수 없다)
        addNounsToDictionary(words)
        KoreanDictionaryProvider.properNouns.addAll(words)
    }

    /**
     * 등록된 금칙어를 제외시킵니다
     *
     * @param words
     * @param severity
     */
    fun removeBlockword(words: List<String>, severity: Severity = Severity.DEFAULT) {
        withBlockwordDictionary(severity) {
            removeAll(words)
        }
    }

    private inline fun withBlockwordDictionary(severity: Severity, action: CharArraySet.() -> Unit) {
        when (severity) {
            LOW  -> {
                KoreanDictionaryProvider.blockWords[LOW]?.action()
                KoreanDictionaryProvider.blockWords[MIDDLE]?.action()
                KoreanDictionaryProvider.blockWords[HIGH]?.action()
            }

            MIDDLE -> {
                KoreanDictionaryProvider.blockWords[MIDDLE]?.action()
                KoreanDictionaryProvider.blockWords[HIGH]?.action()
            }

            else ->
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
    fun splitSentences(text: CharSequence): Sequence<Sentence> = KoreanSentenceSplitter.split(text)

    /**
     * Split input text into sentences.
     *
     * @param text input text
     * @return A sequence of sentences.
     */
    fun splitSentencesAsync(text: CharSequence): Deferred<Sequence<Sentence>> {
        return scope.async(Dispatchers.Default) {
            KoreanSentenceSplitter.split(text)
        }
    }


    /**
     * Extract noun-phrases from Korean text
     *
     * @param tokens         Korean tokens
     * @param filterSpam     true if spam/slang terms to be filtered out (default: false)
     * @param enableHashtags true if #hashtags to be included (default: true)
     * @return A sequence of extracted phrases
     */
    @JvmOverloads
    fun extractPhrases(
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
     * @param filterSpam     true if spam/slang terms to be filtered out (default: false)
     * @param enableHashtags true if #hashtags to be included (default: true)
     * @return A sequence of extracted phrases
     */
    @JvmOverloads
    fun extractPhrasesAsync(
        tokens: List<KoreanToken>,
        filterSpam: Boolean = false,
        enableHashtags: Boolean = true,
    ): Deferred<List<KoreanPhrase>> {
        return scope.async {
            KoreanPhraseExtractor.extractPhrases(tokens, filterSpam, enableHashtags)
        }
    }

    /**
     * Extract noun-phrases from Korean text
     *
     * @param tokens         Korean tokens
     * @return A sequence of extracted phrases
     */
    fun extractPhrasesForProduct(tokens: List<KoreanToken>): List<KoreanPhrase> {
        return NounPhraseExtractor.extractPhrases(tokens)
    }

    /**
     * Extract noun-phrases from Korean text
     *
     * @param tokens         Korean tokens
     * @return A sequence of extracted phrases
     */
    fun extractPhrasesAsyncProduct(tokens: List<KoreanToken>): Deferred<List<KoreanPhrase>> {
        return scope.async {
            NounPhraseExtractor.extractPhrases(tokens)
        }
    }

    /**
     * Removes Ending tokens recovering the root form of predicates
     *
     * @param tokens A sequence of tokens
     * @return A sequence of collapsed Korean tokens
     */
    fun stem(tokens: List<KoreanToken>): List<KoreanToken> {
        return KoreanStemmer.stem(tokens)
    }


    /**
     * Detokenize the input list of words.
     *
     * @param tokens List of words.
     * @return Detokenized string.
     */
    fun detokenize(tokens: Collection<String>): String =
        KoreanDetokenizer.detokenize(tokens)

    /**
     * Detokenize the input list of words.
     *
     * @param tokens List of words.
     * @return Detokenized string.
     */
    fun detokenizeAsync(tokens: Collection<String>): Deferred<String> {
        return scope.async {
            KoreanDetokenizer.detokenize(tokens)
        }
    }

    /**
     * 금칙어 (Block words) 를 masking 합니다.
     *
     * 예: 미니미와 니미 -> 미니미와 **        // `니미` 는 속어
     *
     * @param request 금칙어 처리 요청 정보 [BlockwordRequest]
     * @return 금칙어를 처리한 결과 [BlockwordResponse]
     */
    fun maskBlockwords(request: BlockwordRequest): BlockwordResponse {
        return BlockwordProcessor.maskBlockwords(request)
    }
}
