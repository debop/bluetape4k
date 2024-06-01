package io.bluetape4k.cache.memorizer.inmemory

import io.bluetape4k.cache.memorizer.AsyncMemorizer
import kotlinx.atomicfu.locks.ReentrantLock
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.withLock

/**
 * 로컬 메모리에 [evaluator] 실행 결과를 저장합니다.
 *
 * @property evaluator 캐시 값을 생성하는 메소드
 */
class AsyncInMemoryMemorizer<in T, R>(
    private val evaluator: (T) -> CompletableFuture<R>,
): AsyncMemorizer<T, R> {

    private val resultCache: MutableMap<T, R> = mutableMapOf()
    private val lock = ReentrantLock()

    override fun invoke(key: T): CompletableFuture<R> {
        val promise = CompletableFuture<R>()

        try {
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
        } catch (e: Throwable) {
            promise.completeExceptionally(e)
        }

        return promise
    }

    override fun clear() {
        lock.withLock {
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
