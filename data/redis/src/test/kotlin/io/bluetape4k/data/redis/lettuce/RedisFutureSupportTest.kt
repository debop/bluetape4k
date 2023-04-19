package io.bluetape4k.data.redis.lettuce

import io.bluetape4k.data.redis.AbstractRedisTest
import io.bluetape4k.data.redis.lettuce.codec.LettuceBinaryCodecs
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.lettuce.core.RedisClient
import io.lettuce.core.api.async.RedisAsyncCommands
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.RepeatedTest

class RedisFutureSupportTest: AbstractRedisTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
        private const val ITEM_SIZE = 1000
    }

    private val client: RedisClient by lazy {
        LettuceClients.clientOf(redis.host, redis.port)
    }
    private val asyncCommands: RedisAsyncCommands<String, Any> by lazy {
        LettuceClients.asyncCommands(client, LettuceBinaryCodecs.Default)
    }

    @AfterAll
    fun cleanup() {
        asyncCommands.shutdown(false)
        client.shutdown()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `bulk put asynchroneously`() = runSuspendWithIO {
        val keyName = randomName()

        val futures = List(ITEM_SIZE) { index ->
            asyncCommands.hset(keyName, index.toString(), index)
        }
        val list = futures.awaitAll()
        list shouldHaveSize ITEM_SIZE

        asyncCommands.hlen(keyName).await().toInt() shouldBeEqualTo ITEM_SIZE

        asyncCommands.del(keyName).await() shouldBeEqualTo 1L
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `sequence for collection of RedisFuture`() {
        val keyName = randomName()

        val futures = List(ITEM_SIZE) { index ->
            asyncCommands.hset(keyName, index.toString(), index)
        }.sequence()

        val list = futures.get()
        list shouldHaveSize ITEM_SIZE

        asyncCommands.hlen(keyName).get().toInt() shouldBeEqualTo ITEM_SIZE
        asyncCommands.del(keyName).get() shouldBeEqualTo 1L
    }
}
