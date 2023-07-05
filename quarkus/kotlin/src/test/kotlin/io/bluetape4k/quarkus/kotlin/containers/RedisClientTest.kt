package io.bluetape4k.quarkus.kotlin.containers

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.quarkus.tests.containers.RedisTestResource
import io.quarkus.redis.datasource.ReactiveRedisDataSource
import io.quarkus.redis.datasource.RedisDataSource
import io.quarkus.test.junit.QuarkusTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import jakarta.inject.Inject

/**
 * Quarkus 의 DevDevices 에서 제공하는 Redis 를 사용합니다.
 *
 * [RedisTestResource] 를 사용하는 것이 아닙니다.
 */
@QuarkusTest
class RedisClientTest {

    companion object: KLogging()

    @Inject
    internal lateinit var redisClient: RedisDataSource

    @Inject
    internal lateinit var reactiveRedisClient: ReactiveRedisDataSource

    @Test
    fun `access redis server`() {
        val key = Fakers.randomUuid().encodeBase62()
        val value = Fakers.randomString()

        val valueCommands = redisClient.value(String::class.java, String::class.java)
        valueCommands.set(key, value)

        val saved = valueCommands.get(key).toString()
        saved shouldBeEqualTo value
    }
}
