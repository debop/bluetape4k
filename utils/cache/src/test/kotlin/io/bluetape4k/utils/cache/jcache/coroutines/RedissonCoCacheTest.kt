package io.bluetape4k.utils.cache.jcache.coroutines

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer
import java.util.UUID
import javax.cache.configuration.MutableConfiguration

class RedissonCoCacheTest: AbstractCoCacheTest() {

    companion object: KLogging() {
        val redisson by lazy { RedisServer.Launcher.RedissonLib.getRedisson() }
    }

    override val coCache: CoCache<String, Any> by lazy {
        RedissonCoCache(
            "coroutine-cache-" + UUID.randomUUID().toString(),
            redisson,
            MutableConfiguration()
        )
    }
}
