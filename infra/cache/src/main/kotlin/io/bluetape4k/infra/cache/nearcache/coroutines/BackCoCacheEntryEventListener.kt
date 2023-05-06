package io.bluetape4k.infra.cache.nearcache.coroutines

import io.bluetape4k.infra.cache.jcache.coroutines.CoCache
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.runBlocking
import javax.cache.event.CacheEntryCreatedListener
import javax.cache.event.CacheEntryEvent
import javax.cache.event.CacheEntryExpiredListener
import javax.cache.event.CacheEntryRemovedListener
import javax.cache.event.CacheEntryUpdatedListener

class BackCoCacheEntryEventListener<K: Any, V: Any>(
    private val targetCache: CoCache<K, V>,
): CacheEntryCreatedListener<K, V>,
    CacheEntryUpdatedListener<K, V>,
    CacheEntryRemovedListener<K, V>,
    CacheEntryExpiredListener<K, V> {

    companion object: KLogging()

    /**
     * Called after one or more entries have been created.
     *
     * @param events The entries just created.
     * @throws CacheEntryListenerException if there is problem executing the listener
     */
    override fun onCreated(events: MutableIterable<CacheEntryEvent<out K, out V>>) {
        log.debug { "BackCache cache entry created. events=${events.joinToString { it.asText() }}" }
        if (!targetCache.isClosed()) {
            runBlocking {
                targetCache.putAll(events.associate { it.key to it.value })
            }
        }
    }

    /**
     * Called after one or more entries have been updated.
     *
     * @param events The entries just updated.
     * @throws CacheEntryListenerException if there is problem executing the listener
     */
    override fun onUpdated(events: MutableIterable<CacheEntryEvent<out K, out V>>) {
        log.debug { "BackCache cache entry updated. events=${events.joinToString { it.asText() }}" }
        if (!targetCache.isClosed()) {
            runBlocking {
                targetCache.putAll(events.associate { it.key to it.value })
            }
        }
    }

    /**
     * Called after one or more entries have been removed. If no entry existed for
     * a key an event is not raised for it.
     *
     * @param events The entries just removed.
     * @throws CacheEntryListenerException if there is problem executing the listener
     */
    override fun onRemoved(events: MutableIterable<CacheEntryEvent<out K, out V>>) {
        log.debug { "BackCache cache entry removed. events=${events.joinToString { it.asText() }}" }
        if (!targetCache.isClosed()) {
            runBlocking {
                targetCache.removeAll(events.map { it.key }.toSet())
            }
        }
    }

    /**
     * Called after one or more entries have been expired by the cache. This is not
     * necessarily when an entry is expired, but when the cache detects the expiry.
     *
     * @param events The entries just removed.
     * @throws CacheEntryListenerException if there is problem executing the listener
     */
    override fun onExpired(events: MutableIterable<CacheEntryEvent<out K, out V>>) {
        log.debug { "BackCache cache entry expired. events=${events.joinToString { it.asText() }}" }
        if (!targetCache.isClosed()) {
            runBlocking {
                targetCache.removeAll(events.map { it.key }.toSet())
            }
        }
    }

    private fun <K, V> CacheEntryEvent<K, V>.asText(): String =
        "source=$source, key=$key, value=$value"
}
