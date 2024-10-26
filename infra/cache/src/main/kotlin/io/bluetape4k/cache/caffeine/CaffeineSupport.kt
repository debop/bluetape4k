package io.bluetape4k.cache.caffeine

import com.github.benmanes.caffeine.cache.AsyncCache
import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.CaffeineSpec
import com.github.benmanes.caffeine.cache.LoadingCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * [CaffeineSpec] 을 빌드합니다.
 *
 * @param specification caffign 환경 설정 정보
 * @return [CaffeineSpec] instance
 */
fun caffeineSpecOf(specification: String): CaffeineSpec =
    CaffeineSpec.parse(specification)

/**
 * [caffeine]을 빌드합니다.
 *
 * @param initializer Caffeine builder method
 * @return [caffeine] instance
 */
inline fun caffeine(initializer: Caffeine<Any, Any>.() -> Unit): Caffeine<Any, Any> =
    Caffeine.newBuilder().apply(initializer)

/**
 * [caffeine]을 빌드합니다.
 *
 * @param initializer Caffeine builder method
 * @return [caffeine] instance
 */
inline fun caffeineWithVirtualThread(initializer: Caffeine<Any, Any>.() -> Unit): Caffeine<Any, Any> =
    Caffeine.newBuilder().executor(Executors.newVirtualThreadPerTaskExecutor()).apply(initializer)

/**
 * Caffeine Cache를 생성합니다.
 *
 * @param K         Cache key type
 * @param V         Cache value type
 * @param builder   Cache builder
 * @return  [Cache] instance
 */
fun <K: Any, V: Any> Caffeine<Any, Any>.cache(): Cache<K, V> = build()

/**
 * Caffeine [LoadingCache]를 빌드합니다.
 *
 * @param K         Cache key type
 * @param V         Cache value type
 * @param builder   Cache builder
 * @return [AsyncCache] instance
 */
fun <K: Any, V: Any> Caffeine<Any, Any>.loadingCache(loader: (K) -> V): LoadingCache<K, V> =
    build { key: K -> loader(key) }


/**
 * Caffeine [AsyncCache]를 빌드합니다.
 *
 * @param K         Cache key type
 * @param V         Cache value type
 * @param builder   Cache builder
 * @return [AsyncCache] instance
 */
fun <K: Any, V: Any> Caffeine<Any, Any>.asyncCache(): AsyncCache<K, V> = buildAsync()


/**
 * Caffeine [AsyncLoadingCache]를 빌드합니다.
 *
 * @param K         Cache key type
 * @param V         Cache value type
 * @param loader    Cache value loader
 * @return [AsyncLoadingCache] instance
 */
inline fun <K: Any, V: Any> Caffeine<Any, Any>.asyncLoadingCache(
    crossinline loader: (key: K) -> CompletableFuture<V>,
): AsyncLoadingCache<K, V> = buildAsync { key: K, _: Executor -> loader(key) }

/**
 * Caffeine [AsyncLoadingCache]를 빌드합니다.
 *
 * @param K         Cache key type
 * @param V         Cache value type
 * @param loader    Cache value loader
 * @return [AsyncLoadingCache] instance
 */
inline fun <K: Any, V: Any> Caffeine<Any, Any>.asyncLoadingCache(
    crossinline loader: (key: K, executor: Executor) -> CompletableFuture<V>,
): AsyncLoadingCache<K, V> =
    buildAsync { key: K, executor: Executor -> loader(key, executor) }


/**
 * Coroutines Suspend 함수를 이용하여 비동기로 캐시 값을 로딩하는 [AsyncLoadingCache]를 빌드합니다.
 *
 * @param K         Cache key type
 * @param V         Cache value type
 * @param loader 값을 로딩하는 suspend 함수
 * @return [AsyncLoadingCache] 함수
 */
inline fun <K: Any, V: Any> Caffeine<Any, Any>.suspendLoadingCache(
    crossinline loader: suspend (key: K) -> V,
): AsyncLoadingCache<K, V> {
    return buildAsync { key, executor: Executor ->
        CoroutineScope(executor.asCoroutineDispatcher()).future {
            loader(key)
        }
    }
}


/**
 * [AsyncCache]에 값이 없으면 [loader]를 이용하여 값을 채우고, 반환합니다.
 *
 * @param key     cache key
 * @param loader  cache value loader
 * @return [CompletableFuture] for cache value
 */
inline fun <K: Any, V: Any> AsyncCache<K, V>.getSuspending(
    key: K,
    crossinline loader: suspend (key: K) -> V,
): CompletableFuture<V> {
    return this.get(key) { k: K, executor: Executor ->
        CoroutineScope(executor.asCoroutineDispatcher()).future {
            loader(k)
        }
    }
}
