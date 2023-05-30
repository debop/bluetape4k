package io.bluetape4k.data.redis.redisson.coroutines

import io.bluetape4k.data.redis.redisson.leader.coroutines.RedissonCoLeaderElection
import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class RedissonClientCoroutineTest: AbstractRedissonCoroutineTest() {

    companion object: KLogging()

    @Test
    fun `with batch async`() = runSuspendWithIO {
        // create map
        val mapName = randomName()
        val map = redisson.getMap<String, String>(mapName)

        try {

            val result = redisson.withBatchSuspending {
                val batchMap = getMap<String, String>(mapName)
                batchMap.fastPutAsync("1", "2")
                batchMap.putAsync("2", "5")
                batchMap.getAllAsync(setOf("1", "2"))
            }
            log.debug { "responses=${result.responses}" }
            result.responses.first() shouldBeEqualTo true
            result.responses.last() shouldBeEqualTo mapOf("1" to "2", "2" to "5")

        } finally {
            map.delete()
        }
    }

    @Test
    fun `use transaction async`() = runSuspendWithIO {
        val map = redisson.getMap<String, String>(randomName())
        val set = redisson.getSet<String>(randomName())

        try {
            val value: String = randomString(32)
            redisson.withTransactionSuspending {
                map.putAsync("1", value).awaitSuspending()
                map.getAsync("3").awaitSuspending()

                set.addAsync(value).awaitSuspending()
            }
            map.getAsync("1").awaitSuspending() shouldBeEqualTo value
            set.containsAsync(value).awaitSuspending().shouldBeTrue()

        } finally {
            map.delete()
            set.delete()
        }
    }

    @Test
    fun `use transaction async in multi jobs`() = runSuspendWithIO {
        val map = redisson.getMap<String, String>(randomName())

        val lockName = randomName()
        val leaderElection = RedissonCoLeaderElection(redissonClient)
        val counter = atomic(0)

        try {
            MultiJobTester()
                .numThreads(4)
                .roundsPerThread(8)
                .add {
                    // redisson.runIfLeaderSuspending(lockName) {
                    leaderElection.runIfLeader(lockName) {
                        val value = randomString(64)
                        redisson.withTransactionSuspending {
                            map.putAsync("1", value).awaitSuspending()
                            map.putAsync("2", value).awaitSuspending()
                            map.putAsync("3", value).awaitSuspending()
                            counter.incrementAndGet()
                        }
                        delay(10L)
                        map.getAsync("1").awaitSuspending() shouldBeEqualTo value
                        map.getAsync("2").awaitSuspending() shouldBeEqualTo value
                        map.getAsync("3").awaitSuspending() shouldBeEqualTo value
                    }
                }
                .run()

            counter.value shouldBeEqualTo 32
        } finally {
            map.delete()
        }
    }
}
