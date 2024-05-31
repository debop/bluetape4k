package io.bluetape4k.support

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class StringBuilderSupportTest {

    companion object: KLogging()

    @Test
    fun `append iterable items`() {
        val list = listOf(1, 2, 3)
        val str = buildString {
            appendItems(list)
        }
        str shouldBeEqualTo "1, 2, 3"
    }

    @Test
    fun `append sequence items`() {
        val list = listOf(1, 2, 3)
        val str = buildString {
            appendItems(list.asSequence())
        }
        str shouldBeEqualTo "1, 2, 3"
    }
}
