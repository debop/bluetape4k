package io.bluetape4k.cache.memorizer.caffeine

import com.github.benmanes.caffeine.cache.Cache
import io.bluetape4k.cache.memorizer.Memorizer
import kotlinx.atomicfu.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Caffeine Cache를 이용하여 메소드의 실행 결과를 기억하여, 재 실행 시에 빠르게 응닫할 수 있도록 합니다.
 *
 * @property cache 실행한 값을 저장할 Cache
 * @property evaluator 캐시 값을 생성하는 메소드
 */
class CaffeineMemorizer<T: Any, R: Any>(
    private val cache: Cache<T, R>,
    private val evaluator: (T) -> R,
): Memorizer<T, R> {

    private val lock = ReentrantLock()

    override fun invoke(key: T): R {
        return cache.getIfPresent(key)
            ?: run {
                val result = evaluator(key)
                cache.put(key, result)
                result
            }
    }

    override fun clear() {
        lock.withLock {
            cache.cleanUp()
        }
    }
}

/**
 * Caffeine Cache 이용하여 [CaffeineMemorizer]를 생성합니다.
 *
 * @param T cache key type
 * @param R cache value type
 * @param evaluator cache value를 반환하는 메소드
 */
fun <T: Any, R: Any> Cache<T, R>.memorizer(function: (T) -> R): CaffeineMemorizer<T, R> =
    CaffeineMemorizer(this, function)
