package io.bluetape4k.infra.cache.ehcache

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.utils.ShutdownQueue
import org.ehcache.Cache
import org.ehcache.CacheManager
import org.ehcache.config.CacheConfiguration
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ConfigurationBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import org.ehcache.config.units.MemoryUnit

val DefaultEhCacheCacheManager: CacheManager by lazy {
    ehcacheManager {
        this.withDefaultClassLoader()
    }
}

inline fun ehcacheManager(initializer: ConfigurationBuilder.() -> Unit): CacheManager {
    val configuration = ConfigurationBuilder.newConfigurationBuilder()
        .withDefaultClassLoader()
        .apply(initializer)
        .build()

    return CacheManagerBuilder.newCacheManager(configuration).apply {
        init()
        ShutdownQueue.register(this)
    }
}

inline fun <reified K: Any, reified V: Any> CacheManager.getOrCreateCache(
    cacheName: String,
    builder: ResourcePoolsBuilder.() -> Unit = {
        this.heap(10_000L, EntryUnit.ENTRIES)
    },
): Cache<K, V> {
    cacheName.requireNotBlank("cacheName")

    return getCache(cacheName, K::class.java, V::class.java)
        ?: run {
            val resourcePools = ResourcePoolsBuilder
                .newResourcePoolsBuilder()
                .offheap(32L, MemoryUnit.MB)
                .apply(builder)
                .build()

            val cacheConfiguration: CacheConfiguration<K, V> = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(K::class.java, V::class.java, resourcePools)
                .withDefaultResilienceStrategy()
                .withDispatcherConcurrency(4)
                .withDefaultDiskStoreThreadPool()
                .build()

            createCache(cacheName, cacheConfiguration)
        }
}
