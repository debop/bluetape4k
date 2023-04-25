package io.bluetape4k.data.redis.redisson

import io.bluetape4k.data.redis.AbstractRedisTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer
import org.redisson.api.RedissonClient

abstract class AbstractRedissonTest: AbstractRedisTest() {

    companion object: KLogging() {
        @JvmStatic
        val redissonClient by lazy {
            RedisServer.Launcher.RedissonLib.getRedisson()
        }
    }

    protected val redisson: RedissonClient get() = redissonClient

    protected fun newRedisson(): RedissonClient {
        val config = RedisServer.Launcher.RedissonLib.getRedissonConfig()
        return redissonClientOf(config)
    }
}
