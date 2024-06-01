package io.bluetape4k.cache.jcache.coroutines

import io.bluetape4k.cache.jcache.JCaching
import io.bluetape4k.coroutines.support.coAwait
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.requireNotBlank
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.joinAll
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.jcache.JCache
import javax.cache.configuration.CacheEntryListenerConfiguration
import javax.cache.configuration.CompleteConfiguration
import javax.cache.configuration.MutableConfiguration

/**
 * Redisson based [CoCache] implementation.
 *
 * @param K key type
 * @param V value type
 * @property cache Redisson [JCache] instance
 */
class RedissonCoCache<K: Any, V: Any>(private val cache: JCache<K, V>): CoCache<K, V> {

    companion object: KLogging() {

        @JvmStatic
        operator fun <K: Any, V: Any> invoke(
            cacheName: String,
            redisson: RedissonClient,
            configuration: CompleteConfiguration<K, V> = MutableConfiguration(),
        ): RedissonCoCache<K, V> {
            cacheName.requireNotBlank("cacheName")
            val jcache = JCaching.Redisson.getOrCreateCache(cacheName, redisson, configuration) as JCache<K, V>
            return RedissonCoCache(jcache)
        }

        @JvmStatic
        inline operator fun <reified K: Any, reified V: Any> invoke(
            cacheName: String,
            config: Config,
            configuration: CompleteConfiguration<K, V> = MutableConfiguration(),
        ): RedissonCoCache<K, V> {
            cacheName.requireNotBlank("cacheName")
            val jcache = JCaching.Redisson.getOrCreate(cacheName, config, configuration) as JCache<K, V>
            return RedissonCoCache(jcache)
        }
    }

    override fun entries(): Flow<CoCacheEntry<K, V>> = flow {
        cache.asSequence().forEach {
            emit(CoCacheEntry(it.key, it.value))
        }
    }

    override suspend fun clear() {
        cache.clearAsync().coAwait()
    }

    override suspend fun close() {
        cache.close()
    }

    override fun isClosed(): Boolean = cache.isClosed

    override suspend fun containsKey(key: K): Boolean {
        return cache.containsKeyAsync(key).coAwait()
    }

    override suspend fun get(key: K): V? {
        return cache.getAsync(key).coAwait()
    }

    override fun getAll(): Flow<CoCacheEntry<K, V>> {
        return entries()
    }

    override fun getAll(keys: Set<K>): Flow<CoCacheEntry<K, V>> = flow {
        cache.getAllAsync(keys).coAwait().forEach { (key, value) ->
            emit(CoCacheEntry(key, value))
        }
    }

    override suspend fun getAndPut(key: K, value: V): V? {
        return get(key).apply { put(key, value) }
    }

    override suspend fun getAndRemove(key: K): V? {
        return cache.getAndRemoveAsync(key).coAwait()
    }

    override suspend fun getAndReplace(key: K, value: V): V? {
        return cache.getAndReplaceAsync(key, value).coAwait()
    }

    override suspend fun put(key: K, value: V) {
        cache.putAsync(key, value).coAwait()
    }

    override suspend fun putAll(map: Map<K, V>) {
        cache.putAllAsync(map).coAwait()
    }

    override suspend fun putAllFlow(entries: Flow<Pair<K, V>>) {
        entries
            .map { cache.putAsync(it.first, it.second).asDeferred() }
            .toList()
            .joinAll()
    }

    override suspend fun putIfAbsent(key: K, value: V): Boolean {
        return cache.putIfAbsentAsync(key, value).coAwait()
    }

    override suspend fun remove(key: K): Boolean {
        return cache.removeAsync(key).coAwait()
    }

    override suspend fun remove(key: K, oldValue: V): Boolean {
        return cache.removeAsync(key, oldValue).coAwait()
    }

    override suspend fun removeAll() {
        cache.removeAll()
    }

    override suspend fun removeAll(keys: Set<K>) {
        cache.removeAllAsync(keys).coAwait()
    }

    override suspend fun replace(key: K, oldValue: V, newValue: V): Boolean {
        return cache.replaceAsync(key, oldValue, newValue).coAwait()
    }

    override suspend fun replace(key: K, value: V): Boolean {
        return cache.replaceAsync(key, value).coAwait()
    }

    override fun registerCacheEntryListener(configuration: CacheEntryListenerConfiguration<K, V>) {
        cache.registerCacheEntryListener(configuration)
    }

    override fun deregisterCacheEntryListener(configuration: CacheEntryListenerConfiguration<K, V>) {
        cache.deregisterCacheEntryListener(configuration)
    }
}