package io.bluetape4k.infra.resilience4j.cache

import io.bluetape4k.infra.cache.jcache.JCaching
import io.bluetape4k.infra.cache.nearcache.NearCache
import io.bluetape4k.infra.cache.nearcache.NearCacheConfig
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer
import org.junit.jupiter.api.BeforeEach
import org.redisson.api.RedissonClient

class NearCacheJCacheCoroutineTest: AbstractJCacheCoroutinesTest() {

    companion object: KLogging() {
        val redisson: RedissonClient by lazy {
            RedisServer.Launcher.RedissonLib.getRedisson()
        }
    }

    override val jcache: NearCache<String, String> by lazy {
        val nearCacheCfg = NearCacheConfig<String, String>()
        val backCache = JCaching.Redisson.getOrCreate<String, String>("back-coroutines", redisson)
        NearCache(nearCacheCfg, backCache)
    }

    @BeforeEach
    override fun setup() {
        jcache.clearAllCache()
        super.setup()
    }
}
