package io.bluetape4k.utils.resilience4j.cache

import io.bluetape4k.utils.resilience4j.cache.impl.CoroutinesCacheImpl
import io.github.resilience4j.cache.event.CacheEvent
import io.github.resilience4j.core.EventConsumer

/**
 * Coroutines 환경 하에서 jCache를 관리할 수 있도록 합니다.
 */
interface CoCache<K: Any, V> {

    companion object {

        fun <K: Any, V> of(jcache: javax.cache.Cache<K, V>): CoCache<K, V> {
            return CoroutinesCacheImpl(jcache)
        }

        fun <K: Any, V> decorateSuspendedSupplier(cache: CoCache<K, V>, loader: suspend () -> V): suspend (K) -> V {
            return cache.decorateSuspendedSupplier(loader)
        }

        fun <K: Any, V> decorateSuspendedFunction(cache: CoCache<K, V>, loader: suspend (K) -> V): suspend (K) -> V {
            return cache.decorateSuspendedFunction(loader)
        }
    }

    /**
     * the cache name
     */
    val name: String

    /**
     * Returns the Metrics of this Cache.
     */
    val metrics: Metrics

    /**
     * Returns an EventPublisher which can be used to register event consumers.
     */
    val eventPublisher: EventPublisher

    /**
     * 캐시된 정보를 로드합니다. 만약 캐시에 없을 시에는 [loader]를 이용하여 정보를 얻어 캐시에 저장하고, 반환합니다.
     *
     * @param cacheKey       Cache Key
     * @param loader    Value loader
     * @return cached value
     */
    suspend fun computeIfAbsent(cacheKey: K, loader: suspend () -> V): V

    /**
     * 해당 키의 캐시 정보가 존재하는지 여부
     *
     * @param cacheKey Cache Key
     * @return Cache된 정보가 있으면 true, 아니면 false
     */
    fun containsKey(cacheKey: K): Boolean

    interface Metrics {

        /**
         * Returns the current number of cache hits
         */
        fun getNumberOfCacheHits(): Long

        /**
         * Retruns the current number of cache misses
         */
        fun getNumberOfCacheMisses(): Long
    }

    /**
     * An EventPublisher which can be used to register event consumers.
     */
    interface EventPublisher: io.github.resilience4j.core.EventPublisher<CacheEvent> {

        fun onCacheHit(eventConsumer: EventConsumer<CacheEvent>): EventPublisher

        fun onCacheMiss(eventConsumer: EventConsumer<CacheEvent>): EventPublisher

        fun onError(eventConsumer: EventConsumer<CacheEvent>): EventPublisher
    }
}
