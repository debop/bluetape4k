package io.bluetape4k.utils.cache.memorizer.inmemory

import io.bluetape4k.utils.cache.memorizer.Memorizer
import java.util.concurrent.ConcurrentHashMap

/**
 * 함수의 실행 결과를 캐시하여, 재 호출 시 캐시된 내용을 제공하도록 합니다.
 *
 * @param evaluator    수행할 함수
 */
class InMemoryMemorizer<in T, out R>(private val evaluator: (T) -> R): Memorizer<T, R> {

    private val resultCache: MutableMap<T, R> = ConcurrentHashMap()

    override fun invoke(key: T): R {
        return resultCache.getOrPut(key) { evaluator(key) }
    }

    override fun clear() {
        synchronized(this) {
            resultCache.clear()
        }
    }
}

/**
 * InMemory 이용하여 [InMemoryMemorizer]를 생성합니다.
 *
 * @param T cache key type
 * @param R cache value type
 * @return [Memorizer] instance
 */
fun <T: Any, R: Any> ((T) -> R).memorizer(): InMemoryMemorizer<T, R> =
    InMemoryMemorizer(this)
