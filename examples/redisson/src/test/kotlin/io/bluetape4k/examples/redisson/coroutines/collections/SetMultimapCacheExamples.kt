package io.bluetape4k.examples.redisson.coroutines.collections

import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.redisson.api.BatchOptions
import java.util.concurrent.TimeUnit

/**
 * Set based Multimap
 *
 * 참고: [Multimap](https://github.com/redisson/redisson/wiki/7.-distributed-collections#72-multimap)
 */
class SetMultimapCacheExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging()

    private suspend fun addSampleData(mapName: String) {
        val batch = redisson.createBatch(BatchOptions.defaults())

        with(batch.getSetMultimapCache<String, Int>(mapName)) {
            putAllAsync("1", listOf(1, 2, 3, 1))
            putAllAsync("2", listOf(5, 6, 5))
            putAsync("4", 7)
            putAsync("2", 5)
        }
        batch.executeAsync().awaitSuspending()
    }

    @Test
    fun `use RSetMultimapCache`() = runTest {
        val mmapName = randomName()
        val mmap = redisson.getSetMultimapCache<String, Int>(mmapName)
        addSampleData(mmapName)

        mmap.getAllAsync("1").awaitSuspending() shouldBeEqualTo setOf(1, 2, 3)

        // expire 설정
        mmap.expireKeyAsync("1", 60, TimeUnit.SECONDS).awaitSuspending()

        mmap.containsEntryAsync("1", 3).awaitSuspending().shouldBeTrue()
        mmap.containsKeyAsync("1").awaitSuspending().shouldBeTrue()
        mmap.containsValueAsync(3).awaitSuspending().shouldBeTrue()


        mmap.entries().forEach { entry ->
            log.debug { "key=${entry.key}, value=${entry.value}" }
        }

        mmap.removeAsync("1", 3).awaitSuspending().shouldBeTrue()
        mmap.getAllAsync("1").awaitSuspending() shouldBeEqualTo setOf(1, 2)

        // put all
        mmap.putAllAsync("5", listOf(5, 6, 7, 8, 9)).awaitSuspending().shouldBeTrue()

        // 기존 List를 반환하고 새로운 값을 설정
        mmap.replaceValuesAsync("2", listOf(5, 6, 7, 8, 9)).awaitSuspending() shouldBeEqualTo setOf(5, 6)

        // RList 를 반환한다
        mmap.get("2").addAsync(100).awaitSuspending()

        // List Value를 반환한다
        mmap.getAllAsync("2").awaitSuspending() shouldBeEqualTo setOf(5, 6, 7, 8, 9, 100)

        // fast remove
        mmap.fastRemoveAsync("2").awaitSuspending() shouldBeEqualTo 1
        // fast remove with not exists key
        mmap.fastRemoveAsync("9999").awaitSuspending() shouldBeEqualTo 0

        mmap.deleteAsync().awaitSuspending()
    }
}