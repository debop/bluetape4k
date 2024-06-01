package io.bluetape4k.hyperscan.block

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test

class BlockwordPatternProviderTest {

    companion object: KLogging()

    @Test
    fun `loading blockword patterns`() {
        BlockwordPatternProvider.blockPatterns.shouldNotBeEmpty()
        BlockwordPatternProvider.blockPatterns.all { it.pattern().isNotEmpty() }.shouldBeTrue()
    }
}
