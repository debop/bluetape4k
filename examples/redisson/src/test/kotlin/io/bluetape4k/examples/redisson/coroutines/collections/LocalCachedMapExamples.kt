package io.bluetape4k.examples.redisson.coroutines.collections

import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.redis.redisson.coroutines.coAwait
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.redisson.api.RLocalCachedMap
import org.redisson.api.options.LocalCachedMapOptions
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

/**
 * Redisson [RLocalCachedMap] 은 NearCache 와 같은 역할을 수행한다.
 *
 * 참고: [Redisson 7.-Distributed-collections](https://github.com/redisson/redisson/wiki/7.-Distributed-collections)
 */
class LocalCachedMapExamples: AbstractRedissonCoroutineTest() {

    @Test
    fun `simple local cached map`() = runTest {
        val cachedMapName = "local:" + UUID.randomUUID().toString()

        val options = LocalCachedMapOptions.name<String, Int>(cachedMapName)
            .cacheSize(10000)
            .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LRU)
            .maxIdle(10.seconds.toJavaDuration())
            .timeToLive(60.seconds.toJavaDuration())


        val cachedMap: RLocalCachedMap<String, Int> = redisson.getLocalCachedMap(options)

        // NOTE: fastPutAsync 의 결과는 new insert 인 경우는 true, update 는 false 를 반환한다.
        cachedMap.fastPutAsync("a", 1).coAwait().shouldBeTrue()
        cachedMap.fastPutAsync("b", 2).coAwait().shouldBeTrue()
        cachedMap.fastPutAsync("c", 3).coAwait().shouldBeTrue()

        cachedMap.containsKeyAsync("a").coAwait().shouldBeTrue()

        cachedMap.getAsync("c").coAwait() shouldBeEqualTo 3
        // FIXME: HINCRBYFLOAT 를 호출한다
        // cachedMap.addAndGetAsync("a", 32).awaitSuspending() shouldBeEqualTo 33

        // 저장된 Int 형태의 저장 크기
        cachedMap.valueSizeAsync("c").coAwait() shouldBeEqualTo 2

        val keys = setOf("a", "b", "c")

        val mapSlice = cachedMap.getAllAsync(keys).coAwait()
        mapSlice shouldBeEqualTo mapOf("a" to 1, "b" to 2, "c" to 3)

        cachedMap.readAllKeySetAsync().coAwait() shouldBeEqualTo setOf("a", "b", "c")
        cachedMap.readAllValuesAsync().coAwait() shouldBeEqualTo listOf(1, 2, 3)
        cachedMap.readAllEntrySetAsync().coAwait()
            .associate { it.key to it.value } shouldBeEqualTo mapOf("a" to 1, "b" to 2, "c" to 3)

        // 신규 Item일 경우 true, Update 시에는 false 를 반환한다
        cachedMap.fastPutAsync("a", 100).coAwait().shouldBeFalse()
        cachedMap.fastPutAsync("d", 33).coAwait().shouldBeTrue()

        // 삭제 시에는 삭제된 갯수를 반환
        cachedMap.fastRemoveAsync("b").coAwait() shouldBeEqualTo 1L

        // Remote 에 저장되었나 본다
        val backendMap = redisson.getMap<String, Int>(cachedMapName)
        backendMap.containsKey("a").shouldBeTrue()
    }
}
