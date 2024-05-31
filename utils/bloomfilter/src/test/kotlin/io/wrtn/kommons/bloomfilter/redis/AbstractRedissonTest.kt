package io.wrtn.kommons.bloomfilter.redis

import io.wrtn.kommons.logging.KLogging
import io.wrtn.kommons.testcontainers.storage.RedisServer
import org.redisson.api.RedissonClient

abstract class AbstractRedissonTest {

    companion object: KLogging() {
        val redis = RedisServer.Launcher.redis
    }

    protected val redisson: RedissonClient
        get() = RedisServer.Launcher.RedissonLib.getRedisson(redis.url)
}
