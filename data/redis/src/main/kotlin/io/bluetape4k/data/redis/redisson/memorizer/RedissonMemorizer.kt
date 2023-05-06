package io.bluetape4k.data.redis.redisson.memorizer

import io.bluetape4k.infra.cache.memorizer.Memorizer
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.redisson.api.RMap

/**
 * [evaluator]가 실행한 결과를 Redis에 저장하고, 재 실행 시에 빠르게 응답할 수 있도록 합니다.
 *
 * @param T
 * @param R
 * @receiver Redisson [RMap] 인스턴스
 * @param evaluator 실행할 함수
 * @return [RedissonMemorizer] instance
 */
fun <T: Any, R: Any> RMap<T, R>.memorizer(evaluator: (T) -> R): RedissonMemorizer<T, R> =
    RedissonMemorizer(this, evaluator)

/**
 * 함수의 실행 결과를 Redis `map`에 저장하고, 재 실행 시에 빠르게 응답할 수 있도록 합니다.
 *
 * @param T
 * @param R
 * @receiver 실행할 함수
 * @param map Redisson [RMap] 인스턴스
 * @return [RedissonMemorizer] instance
 */
fun <T: Any, R: Any> ((T) -> R).memorizer(map: RMap<T, R>): RedissonMemorizer<T, R> =
    RedissonMemorizer(map, this)

/**
 * [evaluator]가 실행한 결과를 [map]에 저장하고, 재 실행 시에 빠르게 응답할 수 있도록 합니다.
 *
 * @property map       Redisson [RMap] 인스턴스
 * @property evaluator 실행할 함수
 */
class RedissonMemorizer<T: Any, R: Any>(
    val map: RMap<T, R>,
    val evaluator: (T) -> R,
): Memorizer<T, R> {

    companion object: KLogging()

    override fun invoke(key: T): R {
        return map.getOrPut(key) { evaluator(key) }
    }

    override fun clear() {
        log.debug { "Clear all memorized values. map=${map.name}" }
        map.clear()
    }
}
