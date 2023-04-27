package io.bluetape4k.data.redis.redisson.coroutines

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class RedissonClientCoroutineTest: AbstractRedissonCoroutineTest() {

    companion object: KLogging()

    @Test
    fun `with batch async`() = runSuspendWithIO {
        // create map
        val mapName = randomName()
        val map = redisson.getMap<String, String>(mapName)

        try {

            val result = redisson.withBatchAwait {
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

        try {
            redisson.withTransactionAwait {
                map.putAsync("1", "2").awaitSuspending()
                val value = map.getAsync("3").awaitSuspending()

                val set = getSet<String>(randomName())
                set.addAsync(value ?: "fallback").awaitSuspending()


            }
        } finally {
            map.delete()
        }
    }
}
