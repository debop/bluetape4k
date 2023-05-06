package io.bluetape4k.utils

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class ComparisonChainTest {

    companion object: KLogging()

    @Test
    fun `compare without properties`() {
        ComparisonChain.start().result shouldBeEqualTo 0
    }

    @Test
    fun `compare with same string and int`() {
        ComparisonChain.start()
            .compare("a", "a")
            .compare(1, 1)
            .compare(1L, 1L)
            .compare(1.0F, 1.0F)
            .compare(1.0, 1.0)
            .result shouldBeEqualTo 0
    }

    @Test
    fun `compare with different string and int`() {
        ComparisonChain.start()
            .compare("b", "a")
            .compare(1, 1)
            .compare(1L, 1L)
            .result shouldBeEqualTo 1

        ComparisonChain.start()
            .compare("a", "b")
            .compare(1, 1)
            .compare(1L, 1L)
            .result shouldBeEqualTo -1
    }

    @Test
    fun `compare true first`() {
        ComparisonChain.start().compareTrueFirst(true, true).result shouldBeEqualTo 0
        ComparisonChain.start().compareTrueFirst(true, false).result shouldBeEqualTo -1
        ComparisonChain.start().compareTrueFirst(false, true).result shouldBeEqualTo 1
        ComparisonChain.start().compareTrueFirst(false, false).result shouldBeEqualTo 0
    }

    @Test
    fun `compare false first`() {
        ComparisonChain.start().compareFalseFirst(true, true).result shouldBeEqualTo 0
        ComparisonChain.start().compareFalseFirst(true, false).result shouldBeEqualTo 1
        ComparisonChain.start().compareFalseFirst(false, true).result shouldBeEqualTo -1
        ComparisonChain.start().compareFalseFirst(false, false).result shouldBeEqualTo 0
    }
}
