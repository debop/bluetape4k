package io.bluetape4k.utils.cache.memorizer.jcache

import io.bluetape4k.utils.cache.jcache.getOrPut
import io.bluetape4k.utils.cache.memorizer.Memorizer
import javax.cache.Cache

/**
 * JCache를 이용하여 메소드의 실행 결과를 기억하여, 재 실행 시에 빠르게 응닫할 수 있도록 합니다.
 *
 * @property jcache 실행한 값을 저장할 Cache
 * @property evaluator 캐시 값을 생성하는 메소드
 */
class JCacheMemorizer<in T: Any, out R: Any>(
    private val jcache: Cache<T, R>,
    private val evaluator: (T) -> R,
): Memorizer<T, R> {

    override fun invoke(key: T): R {
        return jcache.getOrPut(key) { evaluator(key) }
    }

    override fun clear() {
        synchronized(this) {
            jcache.clear()
        }
    }
}

/**
 * JCache를 이용하여 [JCacheMemorizer]를 생성합니다.
 *
 * @param T cache key type
 * @param R cache value type
 * @param evaluator cache value를 반환하는 메소드
 */
fun <T: Any, R: Any> Cache<T, R>.memorizer(evaluator: (T) -> R): JCacheMemorizer<T, R> =
    JCacheMemorizer(this, evaluator)
