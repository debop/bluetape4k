package io.bluetape4k.tokenizer.korean.utils

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedMap
import io.bluetape4k.collections.eclipse.unifiedMapOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.tokenizer.korean.utils.KoreanConjugation.conjugatePredicatesToCharArraySet
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adjective
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adverb
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Conjunction
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Determiner
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Eomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Exclamation
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Josa
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Modifier
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Noun
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.PreEomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Suffix
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Verb
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.VerbPrefix
import io.bluetape4k.tokenizer.model.Severity
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import java.util.zip.GZIPInputStream


/**
 * Provides a sigleton Korean dictionary
 */
object KoreanDictionaryProvider: KLogging(), Serializable {

    // TODO: Kotlin Coroutines 를 이용한 Async IO 로 구현
    //
    private fun readStreamByLine(stream: InputStream): List<String> {
        return InputStreamReader(stream, Charsets.UTF_8)
            .buffered()
            .use { reader ->
                reader.lineSequence()
                    .filter { it.isNotBlank() }
                    .map(String::trim)
                    .toFastList()
            }
    }

    fun readFileByLineFromResources(filename: String): List<String> {
        log.debug { "Read a file. filename=[$filename]" }

        val filepath = "koreantext/$filename"
        var stream = Thread.currentThread().contextClassLoader.getResourceAsStream(filepath)
        check(stream != null) { "Can't open file. filename=[$filename]" }

        if (filename.endsWith(".gz")) {
            stream = GZIPInputStream(stream)
        }
        return readStreamByLine(stream)
    }

    private fun readWordFreqs(filename: String): Map<CharSequence, Float> {
        val freqRange = 0 until 6
        return readFileByLineFromResources(filename)
            .filter { it.contains("\t") }
            .map {
                val data = it.split("\t", limit = 2)
                data[0] to data[1].slice(freqRange).toFloat()
            }
            .toUnifiedMap()
    }

    private fun readWordMap(filename: String): Map<String, String> {
        return readFileByLineFromResources(filename)
            .filter { it.contains(" ") }
            .map {
                val data = it.split(" ", limit = 2)
                data[0] to data[1]
            }
            .toUnifiedMap()
    }

    fun readWordsAsSeq(filename: String): Sequence<String> {
        return readFileByLineFromResources(filename).asSequence()
    }

    fun readWordsAsSet(vararg filenames: String): MutableSet<String> {
        return filenames
            .flatMap { readFileByLineFromResources(it) }
            .toMutableSet()
    }

    fun readWords(vararg filenames: String): CharArraySet {
        val set = newCharArraySet()
        filenames
            .flatMap { readFileByLineFromResources(it) }
            .forEach(set::add)
        return set
    }

    fun newCharArraySet(): CharArraySet = CharArraySet(10_000)

    val koreanEntityFreq: Map<CharSequence, Float> by lazy {
        readWordFreqs("freq/entity-freq.txt.gz")
    }

    fun addWordsToDictionary(pos: KoreanPos, words: Collection<String>) {
        koreanDictionary[pos]?.addAll(words)
    }

    fun addWordsToDictionary(pos: KoreanPos, vararg words: String) {
        if (words.isNotEmpty()) {
            koreanDictionary[pos]?.addAll(words)
        }
    }

    val koreanDictionary: MutableMap<KoreanPos, CharArraySet> by lazy {
        unifiedMapOf<KoreanPos, CharArraySet>().apply {
            put(
                Noun,
                readWords(
                    "noun/nouns.txt",
                    "noun/entities.txt",
                    "noun/spam.txt",
                    "noun/names.txt",
                    "noun/twitter.txt",
                    "noun/lol.txt",
                    "noun/slangs.txt",
                    "noun/company_names.txt",
                    "noun/foreign.txt",
                    "noun/geolocations.txt",
                    "noun/profane.txt",
                    "substantives/given_names.txt",
                    "noun/kpop.txt",
                    "noun/bible.txt",
                    "noun/pokemon.txt",
                    "noun/congress.txt",
                    "noun/wikipedia_title_nouns.txt",
                    "noun/brand.txt",
                    "noun/fashion.txt",
                    "noun/neologism.txt"
                )
            )

            put(Verb, conjugatePredicatesToCharArraySet(readWordsAsSet("verb/verb.txt")))
            put(
                Adjective,
                conjugatePredicatesToCharArraySet(readWordsAsSet("adjective/adjective.txt"), true)
            )
            put(Adverb, readWords("adverb/adverb.txt"))
            put(Determiner, readWords("auxiliary/determiner.txt"))
            put(Exclamation, readWords("auxiliary/exclamation.txt"))
            put(Josa, readWords("josa/josa.txt"))
            put(Eomi, readWords("verb/eomi.txt"))
            put(PreEomi, readWords("verb/pre_eomi.txt"))
            put(Conjunction, readWords("auxiliary/conjunctions.txt"))
            put(Modifier, readWords("substantives/modifier.txt"))
            put(VerbPrefix, readWords("verb/verb_prefix.txt"))
            put(Suffix, readWords("substantives/suffix.txt"))
        }
    }

    val spamNouns by lazy {
        readWords(
            "noun/spam.txt",
            "noun/profane.txt",
            "noun/slangs.txt",
        )
    }

    /**
     * 금칙어를 심각도에 따라 분류한 Dictionary 입니다.
     */
    val blockWords by lazy {
        unifiedMapOf(
            Severity.LOW to readWords("block/block_low.txt", "block/block_middle.txt", "block/block_high.txt"),
            Severity.MIDDLE to readWords("block/block_middle.txt", "block/block_high.txt"),
            Severity.HIGH to readWords("block/block_high.txt"),
        )
    }

    val properNouns by lazy {
        readWords(
            "noun/entities.txt",
            "noun/names.txt",
            "noun/twitter.txt",
            "noun/lol.txt",
            "noun/company_names.txt",
            "noun/foreign.txt",
            "noun/geolocations.txt",
            "substantives/given_names.txt",
            "noun/kpop.txt",
            "noun/bible.txt",
            "noun/pokemon.txt",
            "noun/congress.txt",
            "noun/wikipedia_title_nouns.txt",
            "noun/brand.txt",
            "noun/fashion.txt",
            "noun/neologism.txt"
        )
    }

    val nameDictionary: Map<String, CharArraySet> by lazy {
        unifiedMapOf(
            "family_name" to readWords("substantives/family_names.txt"),
            "given_name" to readWords("substantives/given_names.txt"),
            "full_name" to readWords("noun/kpop.txt", "noun/foreign.txt", "noun/names.txt")
        )
    }

    val typoDictionaryByLength: Map<Int, Map<String, String>> by lazy {
        val grouped = readWordMap("typos/typos.txt").entries.groupBy { it.key.length }
        val result = unifiedMapOf<Int, Map<String, String>>()

        grouped.forEach { (index, list) ->
            result[index] = list.map { (k, v) -> k to v }.toUnifiedMap()
        }

        result
    }

    val predicateStems: Map<KoreanPos, Map<String, String>> by lazy {
        fun getConjugationMap(words: Set<String>, isAdjective: Boolean): Map<String, String> {
            return words
                .flatMap { word ->
                    KoreanConjugation.conjugatePredicated(hashSetOf(word), isAdjective).map { Pair(it, word + "다") }
                }
                .toUnifiedMap()
        }

        unifiedMapOf(
            Verb to getConjugationMap(readWordsAsSet("verb/verb.txt"), false),
            Adjective to getConjugationMap(readWordsAsSet("adjective/adjective.txt"), true)
        )
    }
}
