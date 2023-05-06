package io.bluetape4k.collections.eclipse

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class FastListSupportTest {

    companion object: KLogging()

    private val expectedList = fastList(10) { it + 1 }

    @Test
    fun `create emptyFastList`() {
        val empty = emptyFastList<Int>()
        empty.size shouldBeEqualTo 0
    }

    @Test
    fun `size가 음수일 때는 예외발생`() {
        assertFailsWith<AssertionError> {
            fastList(-1) { it }
        }
    }

    @Test
    fun `size가 0인 경우 emptry list 반환`() {
        val emptyList = fastList(0) { it }
        emptyList.size shouldBeEqualTo 0
    }

    @Test
    fun `size 와 initializer를 이용하여 FastList 생성하기`() {
        val list = fastList(10) { it }
        list shouldBeEqualTo List(10) { it }
    }

    @Test
    fun `Iterable 로 FastList 만들기`() {
        (1..10).toFastList() shouldBeEqualTo expectedList
    }

    @Test
    fun `Sequence 로 FastList 만들기`() {
        (1..10).asSequence().toFastList() shouldBeEqualTo expectedList
    }

    @Test
    fun `Iterator 로 FastList 만들기`() {
        (1..10).iterator().toFastList() shouldBeEqualTo expectedList
    }

    @Test
    fun `Array 로 FastList 만들기`() {
        (1..10).toList().toTypedArray().toFastList() shouldBeEqualTo expectedList
    }
}
