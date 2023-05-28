package io.bluetape4k.tokenizer.korean.utils

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
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
    private fun readStreamByLine(stream: InputStream): Flow<String> {
        return flow {
            InputStreamReader(stream, Charsets.UTF_8).buffered().use { reader ->
                reader.lineSequence()
                    .filter { it.isNotBlank() }
                    .forEach {
                        emit(it.trim())
                    }
            }
        }
    }

    fun readFileByLineFromResources(filename: String): Flow<String> {
        log.debug { "Read a file. filename=[$filename]" }

        val filepath = "koreantext/$filename"
        var stream = Thread.currentThread().contextClassLoader.getResourceAsStream(filepath)
        check(stream != null) { "Can't open file. filename=[$filename]" }

        if (filename.endsWith(".gz")) {
            stream = GZIPInputStream(stream)
        }
        return readStreamByLine(stream)
    }

    private suspend fun readWordFreqs(filename: String): Map<CharSequence, Float> {
        val freqRange = 0 until 6
        val map = mutableMapOf<CharSequence, Float>()

        readFileByLineFromResources(filename)
            .filter { it.contains("\t") }
            .map {
                val data = it.split("\t", limit = 2)
                data[0] to data[1].slice(freqRange).toFloat()
            }
            .collect {
                map[it.first] = it.second
            }
        return map
    }

    private fun readWordMap(filename: String): Flow<Pair<String, String>> {
        return readFileByLineFromResources(filename)
            .buffer()
            .filter { it.contains(" ") }
            .map {
                val data = it.split(" ", limit = 2)
                data[0] to data[1]
            }
    }

    fun readWordsAsFlow(filename: String): Flow<String> {
        return readFileByLineFromResources(filename)
    }

    suspend fun readWordsAsSet(vararg filenames: String): MutableSet<String> {
        val set = mutableSetOf<String>()
        filenames.asFlow()
            .flatMapMerge {
                readFileByLineFromResources(it)
            }
            .collect {
                set.add(it)
            }
        return set
    }

    suspend fun readWords(vararg filenames: String): CharArraySet {
        val set = newCharArraySet()
        filenames.asFlow()
            .flatMapMerge { readFileByLineFromResources(it) }
            .collect {
                set.add(it)
            }
        return set
    }

    fun newCharArraySet(): CharArraySet = CharArraySet(10_000)

    val koreanEntityFreq: Map<CharSequence, Float> by lazy {
        runBlocking(Dispatchers.IO) {
            readWordFreqs("freq/entity-freq.txt.gz")
        }
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
        runBlocking(Dispatchers.IO) {
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
    }

    val spamNouns by lazy {
        runBlocking(Dispatchers.IO) {
            readWords(
                "noun/spam.txt",
                "noun/profane.txt",
                "noun/slangs.txt",
            )
        }
    }

    /**
     * 금칙어를 심각도에 따라 분류한 Dictionary 입니다.
     */
    val blockWords by lazy {
        runBlocking(Dispatchers.IO) {
            unifiedMapOf(
                Severity.LOW to readWords("block/block_low.txt", "block/block_middle.txt", "block/block_high.txt"),
                Severity.MIDDLE to readWords("block/block_middle.txt", "block/block_high.txt"),
                Severity.HIGH to readWords("block/block_high.txt"),
            )
        }
    }

    val properNouns by lazy {
        runBlocking(Dispatchers.IO) {
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
    }

    val nameDictionary: Map<String, CharArraySet> by lazy {
        runBlocking(Dispatchers.IO) {
            unifiedMapOf(
                "family_name" to readWords("substantives/family_names.txt"),
                "given_name" to readWords("substantives/given_names.txt"),
                "full_name" to readWords("noun/kpop.txt", "noun/foreign.txt", "noun/names.txt")
            )
        }
    }

    val typoDictionaryByLength: Map<Int, Map<String, String>> by lazy {
        runBlocking(Dispatchers.IO) {
            val grouped = readWordMap("typos/typos.txt").toList().groupBy { it.first.length }
            val result = unifiedMapOf<Int, Map<String, String>>()

            grouped.forEach { (index, pair) ->
                result[index] = pair.map { (k, v) -> k to v }.toUnifiedMap()
            }

            result
        }
    }

    val predicateStems: Map<KoreanPos, Map<String, String>> by lazy {
        fun getConjugationMap(words: Set<String>, isAdjective: Boolean): Map<String, String> {
            return words
                .flatMap { word ->
                    KoreanConjugation.conjugatePredicated(hashSetOf(word), isAdjective).map { Pair(it, word + "다") }
                }
                .toUnifiedMap()
        }
        runBlocking(Dispatchers.IO) {
            unifiedMapOf(
                Verb to getConjugationMap(readWordsAsSet("verb/verb.txt"), false),
                Adjective to getConjugationMap(readWordsAsSet("adjective/adjective.txt"), true)
            )
        }
    }
}
