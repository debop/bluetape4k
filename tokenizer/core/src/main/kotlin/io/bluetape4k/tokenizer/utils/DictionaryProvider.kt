package io.bluetape4k.tokenizer.utils

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flatMapMerge
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.zip.GZIPInputStream

/**
 * 사전 내용을 파일로부터 읽어서 제공합니다.
 */
object DictionaryProvider: KLogging() {

    private const val SPACE = " "
    private const val TAB = "\t"

    fun readStreamByLine(stream: InputStream): Sequence<String> {
        return InputStreamReader(stream, Charsets.UTF_8)
            .buffered()
            .lineSequence()
            .map { it.trim() }
    }

    fun readFileByLineFromResources(path: String, classLoader: ClassLoader? = null): Sequence<String> {
        log.debug { "Read a file. path=$path" }

        val stream = (classLoader ?: Thread.currentThread().contextClassLoader).getResourceAsStream(path)
        check(stream != null) { "Can't open file. path=$path" }

        return if (path.endsWith(".gz")) {
            readStreamByLine(GZIPInputStream(stream))
        } else {
            readStreamByLine(stream)
        }
    }

    fun readWordFreqs(path: String): Map<CharSequence, Float> {
        val freqRange = 0 until 6
        val map = ConcurrentHashMap<CharSequence, Float>()

        readFileByLineFromResources(path)
            .filter { it.contains(TAB) }
            .map {
                val elems = it.split(TAB, limit = 2)
                elems[0] to elems[1].slice(freqRange).toFloat()
            }
            .forEach {
                map[it.first] = it.second
            }

        return map
    }

    fun readWordMap(filename: String): Sequence<Pair<String, String>> {
        return readFileByLineFromResources(filename)
            .filter { it.contains(SPACE) }
            .map {
                val words = it.split(SPACE, limit = 2)
                words[0] to words[1]
            }
    }

    fun readWordsAsSequence(filename: String): Sequence<String> {
        return readFileByLineFromResources(filename)
    }

    suspend fun readWordsAsSet(vararg paths: String): MutableSet<String> {
        val set = ConcurrentSkipListSet<String>()
        paths.asFlow()
            .buffer()
            .flatMapMerge { path -> readFileByLineFromResources(path).asFlow() }
            .collect { word -> set.add(word) }

        return set
    }

    suspend fun readWords(vararg paths: String): CharArraySet {
        val set = newCharArraySet()
        paths.asFlow()
            .buffer()
            .flatMapMerge { path -> readFileByLineFromResources(path).asFlow() }
            .collect { word -> set.add(word) }
        return set
    }

    fun newCharArraySet(): CharArraySet {
        return CharArraySet(5_000)
    }
}
