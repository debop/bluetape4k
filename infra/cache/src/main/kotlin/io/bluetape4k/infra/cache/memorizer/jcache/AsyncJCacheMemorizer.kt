package io.bluetape4k.infra.cache.memorizer.jcache

import io.bluetape4k.infra.cache.memorizer.AsyncMemorizer
import java.util.concurrent.CompletableFuture
import javax.cache.Cache

/**
 * JCache를 이용하여 메소드의 실행 결과를 캐시하여 , 재 실행 시에 빠르게 응닫할 수 있도록 합니다.
 *
 * @property jcache 실행한 값을 저장할 Cache
 * @property evaluator 캐시 값을 생성하는 메소드
 */
class AsyncJCacheMemorizer<in T: Any, R: Any>(
    private val jcache: Cache<T, R>,
    private val evaluator: (T) -> CompletableFuture<R>,
): AsyncMemorizer<T, R> {

    override fun invoke(p1: T): CompletableFuture<R> {
        val promise = CompletableFuture<R>()

        val value = jcache.get(p1)
        if (value != null) {
            promise.complete(value)
        } else {
            evaluator(p1)
                .whenComplete { result, error ->
                    if (error != null)
                        promise.completeExceptionally(error)
                    else {
                        jcache.put(p1, result)
                        promise.complete(result)
                    }
                }
        }

        return promise
    }

    override fun clear() {
        synchronized(this) {
            jcache.clear()
        }
    }
}

/**
 * JCache를 이용하는 [JCacheAsyncMemorizer]를 생성합니다.
 *
 * @param T cache key type
 * @param R cache value type
 * @param evaluator cache value를 반환하는 메소드
 */
fun <T: Any, R: Any> Cache<T, R>.asyncMemorizer(evaluator: (T) -> CompletableFuture<R>): AsyncJCacheMemorizer<T, R> =
    AsyncJCacheMemorizer(this, evaluator)
