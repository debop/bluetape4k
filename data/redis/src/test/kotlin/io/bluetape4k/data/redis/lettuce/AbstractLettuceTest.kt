package io.bluetape4k.data.redis.lettuce

import io.bluetape4k.data.redis.AbstractRedisTest
import io.bluetape4k.data.redis.lettuce.codec.LettuceBinaryCodecs
import io.lettuce.core.RedisClient
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

abstract class AbstractLettuceTest: AbstractRedisTest() {

    protected lateinit var client: RedisClient

    protected lateinit var commands: RedisCommands<String, Any>
    protected lateinit var asyncCommands: RedisAsyncCommands<String, Any>

    @BeforeAll
    open fun beforeAll() {
        client = LettuceClients.clientOf(redis.host, redis.port)

        commands = LettuceClients.commands(client, LettuceBinaryCodecs.Default)
        asyncCommands = LettuceClients.asyncCommands(client, LettuceBinaryCodecs.Default)
    }

    @AfterAll
    open fun afterAll() {
        client.shutdown()
    }
}
