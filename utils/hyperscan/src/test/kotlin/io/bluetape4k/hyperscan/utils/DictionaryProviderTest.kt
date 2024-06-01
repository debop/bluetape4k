package io.bluetape4k.hyperscan.utils

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class DictionaryProviderTest {

    companion object: KLogging()

    @ParameterizedTest
    @ValueSource(
        strings = [
            "block_patterns.txt",
            "harmful_domains.txt",
        ]
    )
    fun `loading dictionary from resources`(filename: String) {
        val dictionary = DictionaryProvider.loadFromResource("block/$filename").toList()
        dictionary.shouldNotBeEmpty()
    }
}
