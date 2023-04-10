package io.bluetape4k.core.utils

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class BoundedStackTest {

    companion object : KLogging()

    @Test
    fun `invalid bound`() {
        assertFailsWith<IllegalArgumentException> {
            val stack = BoundedStack<Int>(0)
        }
    }

    @Test
    fun `insert item to stack`() {
        val stack = BoundedStack<Int>(3)
        stack.push(1)
        stack.push(2)
        stack.push(3)

        // 기존 요소를 제거하고 추가한다 
        stack.push(4) shouldBeEqualTo 4
        stack.push(5) shouldBeEqualTo 5

        stack.pop() shouldBeEqualTo 5
        stack.pop() shouldBeEqualTo 4
        stack.pop() shouldBeEqualTo 3

        // 더 이상 요소가 없다
        assertFailsWith<NoSuchElementException> {
            stack.pop()
        }
    }
}
