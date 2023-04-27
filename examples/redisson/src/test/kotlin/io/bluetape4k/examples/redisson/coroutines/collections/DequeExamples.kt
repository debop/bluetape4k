package io.bluetape4k.examples.redisson.coroutines.collections

import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class DequeExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging()

    @Test
    fun `deque 사용`() = runTest {
        val deque = redisson.getDeque<String>(randomName())
        deque.clear()

        // push 는 addFirst 와 같다
        // add 는 addLast 와 같다
        deque.addLastAsync("1").awaitSuspending()
        deque.addLastAsync("2").awaitSuspending()
        deque.addLastAsync("3").awaitSuspending()
        deque.addLastAsync("4").awaitSuspending()

        deque.containsAsync("1").awaitSuspending().shouldBeTrue()

        // 첫번째 요소를 조회한다. (제거하지 않는다)
        deque.peekAsync().awaitSuspending() shouldBeEqualTo "1"

        // 첫번째 요소를 가져오고, queue에서는 제거한다 (첫번째 요소가 없으면 들어올 때까지 대기힌다.)
        deque.popAsync().awaitSuspending() shouldBeEqualTo "1"

        // 첫 번째 요소를 조회한다 (제거하지 않는다) 단 queue에 요소가 없으면 예외를 일으킨다
        deque.element() shouldBeEqualTo "2"

        deque.removeAllAsync(listOf("2", "3")).awaitSuspending().shouldBeTrue()

        deque.addAllAsync(listOf("10", "11", "12")).awaitSuspending().shouldBeTrue()

        deque.deleteAsync().awaitSuspending()
    }
}
