package io.bluetape4k.quarkus.kotlin.containers

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.quarkus.tests.containers.RedisTestResource
import io.quarkus.redis.client.RedisClient
import io.quarkus.redis.client.reactive.ReactiveRedisClient
import io.quarkus.test.junit.QuarkusTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import javax.inject.Inject

/**
 * Quarkus 의 DevDevices 에서 제공하는 Redis 를 사용합니다.
 *
 * [RedisTestResource] 를 사용하는 것이 아닙니다.
 */
@QuarkusTest
class RedisClientTest {

    companion object: KLogging()

    @Inject
    internal lateinit var redisClient: RedisClient

    @Inject
    internal lateinit var reactiveRedisClient: ReactiveRedisClient

    @Test
    fun `access redis server`() {
        val key = Fakers.randomUuid().encodeBase62()
        val value = Fakers.randomString()

        redisClient.set(listOf(key, value))

        val saved = redisClient.get(key).toString()
        saved shouldBeEqualTo value
    }
}
