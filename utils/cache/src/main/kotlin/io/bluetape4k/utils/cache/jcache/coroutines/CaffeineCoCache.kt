package io.bluetape4k.utils.cache.jcache.coroutines

import com.github.benmanes.caffeine.cache.AsyncCache
import com.github.benmanes.caffeine.cache.Caffeine
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import java.util.concurrent.CompletableFuture
import javax.cache.configuration.CacheEntryListenerConfiguration
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future

class CaffeineCoCache<K: Any, V: Any>(private val cache: AsyncCache<K, V>): CoCache<K, V> {

    companion object: KLogging() {
        inline operator fun <reified K: Any, reified V: Any> invoke(
            setup: Caffeine<Any, Any>.() -> Unit = {},
        ): CaffeineCoCache<K, V> {
            val asyncCache = Caffeine.newBuilder().apply(setup).buildAsync<K, V>()
            return CaffeineCoCache(asyncCache)
        }
    }

    private var closed: Boolean = false

    override fun entries(): Flow<CoCacheEntry<K, V>> = flow {
        cache.asMap().entries.forEach { (key, valueFuture) ->
            emit(CoCacheEntry(key, valueFuture.await()))
        }
    }

    override suspend fun clear() {
        cache.synchronous().invalidateAll()
    }

    override suspend fun close() {
        closed = true
    }

    override fun isClosed(): Boolean = closed

    override suspend fun containsKey(key: K): Boolean {
        return cache.getIfPresent(key)?.await() != null
    }

    override suspend fun get(key: K): V? {
        return cache.getIfPresent(key)?.await()
    }

    override fun getAll(): Flow<CoCacheEntry<K, V>> {
        return entries()
    }

    override fun getAll(keys: Set<K>): Flow<CoCacheEntry<K, V>> = flow {
        keys.forEach { keyToLoad ->
            val value = get(keyToLoad)
            if (value != null) {
                emit(CoCacheEntry(keyToLoad, value))
            }
        }
    }

    override suspend fun getAndPut(key: K, value: V): V? {
        return get(key).apply { put(key, value) }
    }

    override suspend fun getAndRemove(key: K): V? {
        return cache.getIfPresent(key)?.await()
            .also { oldValue -> oldValue?.run { remove(key) } }
    }

    override suspend fun getAndReplace(key: K, value: V): V? {
        return cache.getIfPresent(key)?.await()?.apply { put(key, value) }
    }

    override suspend fun put(key: K, value: V) {
        cache.put(key, CompletableFuture.completedFuture(value))
    }

    override suspend fun putAll(map: Map<K, V>) {
        cache.synchronous().putAll(map)
    }

    override suspend fun putAllFlow(entries: Flow<Pair<K, V>>) {
        entries.onEach { put(it.first, it.second) }.collect()
    }

    override suspend fun putIfAbsent(key: K, value: V): Boolean {
        return coroutineScope {
            if (!containsKey(key)) {
                cache.put(key, future { value })
                true
            } else {
                false
            }
        }
    }

    override suspend fun remove(key: K): Boolean {
        if (containsKey(key)) {
            cache.synchronous().invalidate(key)
            return true
        }
        return false
    }

    override suspend fun remove(key: K, oldValue: V): Boolean {
        if (get(key) == oldValue) {
            cache.synchronous().invalidate(key)
            return true
        }
        return false
    }

    override suspend fun removeAll() {
        cache.synchronous().invalidateAll()
    }

    override suspend fun removeAll(keys: Set<K>) {
        cache.synchronous().invalidateAll(keys)
    }

    override suspend fun replace(key: K, oldValue: V, newValue: V): Boolean {
        if (get(key) == oldValue) {
            put(key, newValue)
            return true
        }
        return false
    }

    override suspend fun replace(key: K, value: V): Boolean {
        if (containsKey(key)) {
            put(key, value)
            return true
        }
        return false
    }

    override fun registerCacheEntryListener(configuration: CacheEntryListenerConfiguration<K, V>) {
        log.debug { "Local Cache에서는 Listener가 필요없습니다." }
    }

    override fun deregisterCacheEntryListener(configuration: CacheEntryListenerConfiguration<K, V>) {
        // Nothing to do
    }
}
