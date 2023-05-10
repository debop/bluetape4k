package io.bluetape4k.infra.resilience4j.cache

import io.bluetape4k.infra.cache.jcache.JCaching
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer
import org.redisson.api.RedissonClient
import javax.cache.Cache

class RedissonJCacheCoroutineTest: AbstractJCacheCoroutinesTest() {

    companion object: KLogging() {
        val redisson: RedissonClient by lazy {
            RedisServer.Launcher.RedissonLib.getRedisson()
        }
    }

    override val jcache: Cache<String, String> by lazy {
        JCaching.Redisson.getOrCreate("redisson.coroutines", redisson)
    }
}
