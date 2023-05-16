package io.bluetape4k.quarkus.kotlin.containers

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.quarkus.kotlin.AbstractQuarkusTest
import io.bluetape4k.quarkus.tests.containers.RedisTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.coroutines.future.await
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.redisson.api.RedissonClient
import org.redisson.api.redisnode.RedisNodes
import javax.inject.Inject

/**
 * Redis 사용하는 데 `redisson-quarkus-20` 라이브러리를 활용하여, quarkus framework에서 redisson 을 활용하는 예제입니다.
 * bluetape4k-testcontainers의 [RedisServer]를 testcontainers로 띄우고,
 * 환경설정에서 `testcontainers.redis.url` 을 이용하여 redisson 이 접속할 주소를 지정합니다.
 *
 * 참고: application.properties
 *
 * ```
 * %test.quarkus.redisson.single-server-config.address=${testcontainers.redis.url}
 * %test.quarkus.redisson.single-server-config.password=null
 * %test.quarkus.redisson.threads=16
 * %test.quarkus.redisson.netty-threads=32
 * ```
 */
@QuarkusTest
@QuarkusTestResource(RedisTestResource::class)
class RedisTestResourceTest: AbstractQuarkusTest() {

    companion object: KLogging()

    @Inject
    internal lateinit var redisson: RedissonClient

    @Test
    fun `context loading`() {
        ::redisson.isInitialized.shouldBeTrue()
    }

    @Test
    fun `ping redis`() {
        val redisSingle = redisson.getRedisNodes(RedisNodes.SINGLE)
        redisSingle.pingAll().shouldBeTrue()
    }


    @Test
    fun `run redisson operations`() {
        val rmap = redisson.getMap<String, String>("RMap")
        rmap["key"] = "value"
        rmap["key"] shouldBeEqualTo "value"
    }

    @Test
    fun `run redisson async operations`() = runTest {
        val rmap = redisson.getMap<String, String>("RMap")

        log.debug { "put async" }
        rmap.fastPutAsync("key", "value").toCompletableFuture().await().shouldBeTrue()

        log.debug { "get async" }
        rmap.getAsync("key").toCompletableFuture().await() shouldBeEqualTo "value"
        rmap.getAsync("key").toCompletableFuture().await() shouldBeEqualTo "value"
    }

}
