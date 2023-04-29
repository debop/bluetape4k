package io.bluetape4k.infra.cache.memorizer.ehcache

import io.bluetape4k.infra.cache.memorizer.AsyncMemorizer
import java.util.concurrent.CompletableFuture
import org.ehcache.Cache

/**
 * Ehcache 이용하여 메소드의 실행 결과를 캐시하여 , 재 실행 시에 빠르게 응닫할 수 있도록 합니다.
 *
 * @property cache 실행한 값을 저장할 Cache
 * @property evaluator 캐시 값을 생성하는 메소드
 */
class AsyncEhCacheMemorizer<in T: Any, R: Any>(
    private val cache: Cache<T, R>,
    private val evaluator: (T) -> CompletableFuture<R>,
): AsyncMemorizer<T, R> {

    override fun invoke(key: T): CompletableFuture<R> {
        val promise = CompletableFuture<R>()

        val value = cache.get(key)
        if (value != null) {
            promise.complete(value)
        } else {
            evaluator(key)
                .whenComplete { result, error ->
                    if (error != null)
                        promise.completeExceptionally(error)
                    else {
                        cache.put(key, result)
                        promise.complete(result)
                    }
                }
        }

        return promise
    }

    override fun clear() {
        synchronized(this) {
            cache.clear()
        }
    }
}

/**
 * Ehcache 이용하는 [AsyncEhCacheMemorizer]를 생성합니다.
 *
 * @param T cache key type
 * @param R cache value type
 * @param evaluator cache value를 반환하는 메소드
 */
fun <T: Any, R: Any> Cache<T, R>.asyncMemorizer(evaluator: (T) -> CompletableFuture<R>): AsyncEhCacheMemorizer<T, R> =
    AsyncEhCacheMemorizer(this, evaluator)
