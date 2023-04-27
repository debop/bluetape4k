package io.bluetape4k.examples.redisson.coroutines.objects

import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.redisson.api.BatchOptions

class BatchExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `복수의 비동기 작업을 Batch로 수행한다`() = runSuspendWithIO {
        val name1 = randomName()
        val name2 = randomName()
        val name3 = randomName()

        val map1 = redisson.getMap<String, String>(name1)
        val map2 = redisson.getMap<String, String>(name2)
        val map3 = redisson.getMap<String, String>(name3)

        val batch = redisson.createBatch(BatchOptions.defaults())

        batch.getMap<String, String>(name1).fastPutAsync("1", "2")
        batch.getMap<String, String>(name2).fastPutAsync("2", "3")
        batch.getMap<String, String>(name3).fastPutAsync("2", "5")

        val future = batch.getAtomicLong("counter").incrementAndGetAsync()
        batch.getAtomicLong("counter").incrementAndGetAsync()


        // 모든 비동기 작업을 Batch로 수행한다.
        val results = batch.executeAsync().awaitSuspending()

        // NOTE: fastPutAsync 의 결과는 new insert 인 경우는 true, update 는 false 를 반환한다.
        results.responses.forEachIndexed { index, result ->
            log.debug { "response[$index]=$result" }
        }
        future.awaitSuspending() shouldBeEqualTo results.responses[3]

        map1.getAsync("1").awaitSuspending() shouldBeEqualTo "2"
        map2.getAsync("2").awaitSuspending() shouldBeEqualTo "3"
        map3.getAsync("2").awaitSuspending() shouldBeEqualTo "5"

        map1.deleteAsync().awaitSuspending()
        map2.deleteAsync().awaitSuspending()
        map3.deleteAsync().awaitSuspending()
    }
}
