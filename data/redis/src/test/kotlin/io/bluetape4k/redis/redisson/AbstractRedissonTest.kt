package io.bluetape4k.redis.redisson

import io.bluetape4k.logging.KLogging
import io.bluetape4k.redis.AbstractRedisTest
import io.bluetape4k.testcontainers.storage.RedisServer
import org.redisson.api.RedissonClient

abstract class AbstractRedissonTest: AbstractRedisTest() {

    companion object: KLogging() {
        @JvmStatic
        protected val redisServer by lazy { RedisServer.Launcher.redis }

        @JvmStatic
        val redissonClient by lazy {
            RedisServer.Launcher.RedissonLib.getRedisson(redisServer.url)
        }
    }

    protected val redisson: RedissonClient get() = redissonClient

    protected fun newRedisson(): RedissonClient {
        val config = RedisServer.Launcher.RedissonLib.getRedissonConfig(redisServer.url)
        return redissonClientOf(config)
    }
}
