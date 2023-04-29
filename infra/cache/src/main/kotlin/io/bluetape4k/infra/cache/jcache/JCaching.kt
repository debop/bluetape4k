package io.bluetape4k.infra.cache.jcache

import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider
import javax.cache.configuration.Configuration
import org.ehcache.jsr107.EhcacheCachingProvider
import org.redisson.api.RedissonClient
import org.redisson.jcache.JCachingProvider
import org.redisson.jcache.configuration.RedissonConfiguration


typealias JCache<K, V> = javax.cache.Cache<K, V>

object JCaching {

    object Caffeine {
        inline fun <reified K, reified V> getOrCreate(
            name: String,
            configuration: Configuration<K, V> = getDefaultJCacheConfiguration(),
        ): JCache<K, V> =
            jcacheManager<CaffeineCachingProvider>().getOrCreate(name, configuration)
    }

    object EhCache {
        inline fun <reified K, reified V> getOrCreate(
            name: String,
            configuration: Configuration<K, V> = getDefaultJCacheConfiguration(),
        ): JCache<K, V> =
            jcacheManager<EhcacheCachingProvider>().getOrCreate(name, configuration)
    }

    object Redisson {
        inline fun <reified K, reified V> getOrCreate(
            name: String,
            redisson: RedissonClient,
            configuration: Configuration<K, V> = getDefaultJCacheConfiguration(),
        ): JCache<K, V> {
            val redissonConfiguration = RedissonConfiguration.fromInstance(redisson, configuration)
            return jcacheManager<JCachingProvider>().getOrCreate(name, redissonConfiguration)
        }

        inline fun <reified K, reified V> getOrCreate(
            name: String,
            redissonConfig: org.redisson.config.Config,
            configuration: Configuration<K, V> = getDefaultJCacheConfiguration(),
        ): JCache<K, V> {
            val redissonConfiguration = RedissonConfiguration.fromConfig(redissonConfig, configuration)
            return jcacheManager<JCachingProvider>().getOrCreate(name, redissonConfiguration)
        }

        fun <K, V> getOrCreateCache(
            cacheName: String,
            redisson: RedissonClient,
            configuration: Configuration<K, V>,
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
