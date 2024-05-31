package io.bluetape4k.testcontainers.storage

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
class RedisServerTest {

    companion object: KLogging()

    @Nested
    inner class UseDefaultPort {
        @Test
        fun `create redis server with default port`() {
            RedisServer(useDefaultPort = true).use { redis ->
                redis.start()
                redis.isRunning.shouldBeTrue()
                redis.port shouldBeEqualTo RedisServer.PORT

                Thread.sleep(100)

                verifyRedisServer(redis)
            }
        }
    }

    @Nested
    inner class UseDockerPort {
        @Test
        fun `create redis server`() {
            RedisServer().use { redis ->
                redis.start()
                redis.isRunning.shouldBeTrue()

                verifyRedisServer(redis)
            }
        }

        @Test
        fun `create redis server by launcher`() {
            RedisServer.Launcher.redis.use { redis ->
                redis.start()
                redis.isRunning.shouldBeTrue()

                verifyRedisServer(redis)
            }
        }
    }

    private fun verifyRedisServer(redisServer: RedisServer) {
        Thread.sleep(10)
        val redisson = redissonClient(redisServer.url)

        val map = redisson.getMap<String, String>("map")
        map.fastPut("key1", "value1")

        val map2 = redisson.getMap<String, String>("map")
        map2["key1"] shouldBeEqualTo "value1"
    }
}
