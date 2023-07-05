package io.bluetape4k.infra.cache.jcache

import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider
import org.ehcache.jsr107.EhcacheCachingProvider
import org.redisson.api.RedissonClient
import org.redisson.jcache.JCachingProvider
import org.redisson.jcache.configuration.RedissonConfiguration
import javax.cache.CacheManager
import javax.cache.configuration.Configuration
import javax.cache.configuration.MutableConfiguration


typealias JCache<K, V> = javax.cache.Cache<K, V>

object JCaching {

    object Cache2k {
        val cacheManager: CacheManager by lazy { jcacheManager<org.cache2k.jcache.provider.JCacheProvider>() }

        inline fun <reified K, reified V> getOrCreate(
            name: String,
            configuration: Configuration<K, V> = getDefaultJCacheConfiguration(),
        ): JCache<K, V> =
            cacheManager.getOrCreate(name, configuration)
    }

    object Caffeine {

        val cacheManager by lazy { jcacheManager<CaffeineCachingProvider>() }

        inline fun <reified K, reified V> getOrCreate(
            name: String,
            configuration: Configuration<K, V> = getDefaultJCacheConfiguration(),
        ): JCache<K, V> =
            cacheManager.getOrCreate(name, configuration)
    }

    object EhCache {
        val cacheManager by lazy { jcacheManager<EhcacheCachingProvider>() }

        inline fun <reified K, reified V> getOrCreate(
            name: String,
            configuration: Configuration<K, V> = getDefaultJCacheConfiguration(),
        ): JCache<K, V> =
            cacheManager.getOrCreate(name, configuration)
    }

    object Redisson {
        val cacheManager by lazy { jcacheManager<JCachingProvider>() }

        inline fun <reified K, reified V> getOrCreate(
            name: String,
            redisson: RedissonClient,
            configuration: Configuration<K, V> = getDefaultJCacheConfiguration(),
        ): JCache<K, V> {
            val redissonConfiguration = RedissonConfiguration.fromInstance(redisson, configuration)
            return cacheManager.getOrCreate(name, redissonConfiguration)
        }

        inline fun <reified K, reified V> getOrCreate(
            name: String,
            redissonConfig: org.redisson.config.Config,
            configuration: Configuration<K, V> = getDefaultJCacheConfiguration(),
        ): JCache<K, V> {
            val redissonConfiguration = RedissonConfiguration.fromConfig(redissonConfig, configuration)
            return cacheManager.getOrCreate(name, redissonConfiguration)
        }

        fun <K, V> getOrCreateCache(
            cacheName: String,
            redisson: RedissonClient,
            configuration: Configuration<K, V> = MutableConfiguration(),
        ): JCache<K, V> {
            return with(jcacheManager<JCachingProvider>()) {
                getCache(cacheName)
                ?: run {
                    val redissonConfiguration = RedissonConfiguration.fromInstance(redisson, configuration)
                    createCache(cacheName, redissonConfiguration)
                }
            }
        }
    }
}
