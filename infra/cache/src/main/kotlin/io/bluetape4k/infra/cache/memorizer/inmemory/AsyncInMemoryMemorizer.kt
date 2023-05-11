package io.bluetape4k.infra.cache.memorizer.inmemory

import io.bluetape4k.collections.eclipse.unifiedMapOf
import io.bluetape4k.infra.cache.memorizer.AsyncMemorizer
import java.util.concurrent.CompletableFuture

/**
 * 로컬 메모리에 [evaluator] 실행 결과를 저장합니다.
 *
 * @property evaluator 캐시 값을 생성하는 메소드
 */
class AsyncInMemoryMemorizer<in T, R>(
    private val evaluator: (T) -> CompletableFuture<R>,
): AsyncMemorizer<T, R> {

    private val resultCache: MutableMap<T, R> = unifiedMapOf()
    private val synchronizedObject = Any()

    override fun invoke(key: T): CompletableFuture<R> {
        val promise = CompletableFuture<R>()

        if (resultCache.containsKey(key)) {
            promise.complete(resultCache[key])
        } else {
            evaluator(key)
                .whenComplete { result, error ->
                    if (error != null) {
                        promise.completeExceptionally(error)
                    } else {
                        resultCache[key] = result
                        promise.complete(resultCache[key])
                    }
                }
        }

        return promise
    }

    override fun clear() {
        synchronized(synchronizedObject) {
            resultCache.clear()
        }
    }
}

/**
 * InMemory 이용하여 [AsyncInMemoryMemorizer]를 생성합니다.
 *
 * @param T cache key type
 * @param R cache value type
 */
fun <T: Any, R: Any> ((T) -> CompletableFuture<R>).asyncMemorizer(): AsyncInMemoryMemorizer<T, R> =
    AsyncInMemoryMemorizer(this)
