package io.bluetape4k.collections.eclipse.ranges

import io.bluetape4k.collections.ranges.charProgressionOf
import io.bluetape4k.collections.ranges.intProgressionOf
import io.bluetape4k.collections.ranges.longProgressionOf
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class ProgressionSupportTest {

    @Test
    fun `convert to CharArrayList`() {
        val chars = charProgressionOf('a', 'z')
        val list = chars.toCharArrayList()
        list.size() shouldBeEqualTo 26

        val reversed = charProgressionOf('z', 'a', -1)
        val reversedList = reversed.toCharArrayList()
        reversedList.size() shouldBeEqualTo 26

        reversedList.reverseThis() shouldBeEqualTo list
    }

    @Test
    fun `convert to IntArrayList`() {
        val ints = intProgressionOf(1, 16, 3)
        val intList = ints.toIntArrayList()
        intList.size() shouldBeEqualTo 6

        val reversed = intProgressionOf(16, 1, -3)
        val reversedList = reversed.toIntArrayList()
        reversedList.size() shouldBeEqualTo 6

        reversedList.reverseThis() shouldBeEqualTo intList
    }

    @Test
    fun `convert to LongArrayList`() {
        val longs = longProgressionOf(1, 16, 3)
        val longList = longs.toLongArrayList()
        longList.size() shouldBeEqualTo 6

        val reversed = longProgressionOf(16, 1, -3)
        val reversedList = reversed.toLongArrayList()
        reversedList.size() shouldBeEqualTo 6

        reversedList.reverseThis() shouldBeEqualTo longList
    }
}
