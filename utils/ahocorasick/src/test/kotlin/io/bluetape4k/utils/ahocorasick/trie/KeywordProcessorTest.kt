package io.bluetape4k.utils.ahocorasick.trie

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class KeywordProcessorTest {

    companion object: KLogging()

    val trie = Trie.builder()
        .addKeywords("NYC")
        .addKeywords("APPL")
        .addKeywords("java_2e", "java programming")
        .addKeywords("PM", "product manager")
        .build()

    val text = "I am a PM for a java_2e platform working from APPL, NYC"

    @Test
    fun `extract keywords`() {
        val emits = trie.parseText(text)
        log.debug { "emits=$emits" }
    }

    @Test
    fun `tokenize keywords`() {
        val tokens = trie.tokenize(text)
        log.debug { "tokens=$tokens" }
    }

    @Test
    fun `replace keywords`() {
        val map = mapOf(
            "APPL" to "Apple",
            "NYC" to "New york",
            "java_2e" to "java programming",
            "PM" to "product manager"
        )

        val replaced = trie.replace(text, map)
        replaced shouldBeEqualTo "I am a product manager for a java programming platform working from Apple, New york"
    }
}
