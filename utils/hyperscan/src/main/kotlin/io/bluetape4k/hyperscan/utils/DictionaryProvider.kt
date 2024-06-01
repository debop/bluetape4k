package io.bluetape4k.hyperscan.utils

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import java.io.InputStream
import java.io.InputStreamReader

object DictionaryProvider: KLogging() {

    fun loadFromResource(path: String, classLoader: ClassLoader? = null): List<String> {
        log.debug { "Read a resource file. path=$path" }

        val stream = (classLoader ?: Thread.currentThread().contextClassLoader).getResourceAsStream(path)
        check(stream != null) { "Can't open file. path=$path" }

        return readFromStream(stream)
    }

    fun readFromStream(stream: InputStream): List<String> {
        return InputStreamReader(stream, Charsets.UTF_8).buffered().use { reader ->
            reader.readLines().map { it.trim() }
        }
    }
}
