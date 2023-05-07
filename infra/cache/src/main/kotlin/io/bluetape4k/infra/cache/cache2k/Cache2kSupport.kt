package io.bluetape4k.infra.cache.cache2k

import io.bluetape4k.core.requireNotBlank
import org.cache2k.Cache
import org.cache2k.Cache2kBuilder
import org.cache2k.CacheManager
import org.cache2k.config.Cache2kConfig

/**
 * Default Cache2k [CacheManager]
 */
val defaultCache2kManager: CacheManager by lazy { CacheManager.getInstance() }

/**
 * [Cache2kBuilder]를 빌드합니다.
 *
 * @param K       cache key type
 * @param V       cache value type
 * @param builder [Cache2kBuilder]를 이용하여 설정하는 코드 block
 * @return [Cache2kBuilder] instance
 */
inline fun <reified K: Any, reified V: Any> cache2k(
    cacheManager: CacheManager = defaultCache2kManager,
    @BuilderInference builder: Cache2kBuilder<K, V>.() -> Unit = {},
): Cache2kBuilder<K, V> {
    return Cache2kBuilder.of(K::class.java, V::class.java).manager(cacheManager).apply(builder)
}

/**
 * [Cache2kConfiguration]을 빌드합니다.
 *
 * @param K       cache key type
 * @param V       cache value type
 * @param name    cache name
 * @param setting [Cache2kConfiguration]을 이용한 설정 코드 block
 * @return [Cache2kConfiguration] instance
 */
inline fun <reified K: Any, reified V: Any> cache2kConfiguration(
    name: String,
    @BuilderInference setting: Cache2kConfig<K, V>.() -> Unit,
): Cache2kConfig<K, V> {
    name.requireNotBlank("name")

    return Cache2kConfig.of(K::class.java, V::class.java)
        .apply { this.name = name }
        .apply(setting)
}

/**
 * [CacheManager]에서 [cacheName]에 해당하는 Cache를 가져온다. 없으면 null 반환
 *
 * @param K       cache key type
 * @param V       cache value type
 * @param cacheName cache name
 * @return 해당 [Cache], 없으면 null을 반환한다.
 */
inline fun <reified K: Any, reified V: Any> getCache2k(cacheName: String): Cache<K, V>? {
    cacheName.requireNotBlank("cacheName")
    return defaultCache2kManager.getCache(cacheName)
}

/**
 * [CacheManager]에서 [cacheName]에 해당하는 Cache를 가져온다. 만약 없다면 새롭게 생성해서 반환한다.
 *
 * @param K       cache key type
 * @param V       cache value type
 * @param cacheName cache name
 * @param builder [Cache2kBuilder]를 이용하여 설정하는 코드 block
 * @return 해당 [Cache], 없으면 null을 반환한다.
 */
inline fun <reified K: Any, reified V: Any> getOrCreateCache2k(
    cacheName: String,
    @BuilderInference builder: Cache2kBuilder<K, V>.() -> Unit = {},
): Cache<K, V> {
    cacheName.requireNotBlank("cacheName")
    return getCache2k(cacheName) ?: cache2k(builder = builder).build()
}
