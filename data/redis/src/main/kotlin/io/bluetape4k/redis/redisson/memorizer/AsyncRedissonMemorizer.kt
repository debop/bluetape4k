package io.bluetape4k.redis.redisson.memorizer

import io.bluetape4k.cache.memorizer.AsyncMemorizer
import io.bluetape4k.concurrent.flatMap
import io.bluetape4k.concurrent.map
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.redisson.api.RMap
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

/**
 * [evaluator] 결과를 Redis에 저장하도록 합니다.
 *
 * @param evaluator 수행할 비동기 함수
 * @return [AsyncRedissonMemorizer] 인스턴스
 */
fun <T: Any, R: Any> RMap<T, R>.asyncMemorizer(evaluator: (T) -> CompletionStage<R>): AsyncRedissonMemorizer<T, R> =
    AsyncRedissonMemorizer(this, evaluator)

/**
 * 함수의 실행 결과를 Redis의 `map`에 저장하도록 합니다.
 *
 * @receiver 실행할 함수
 * @property map 수행결과를 저장할 Redisson [RMap] 인스턴스
 * @return [AsyncRedissonMemorizer] 인스턴스
 */
fun <T: Any, R: Any> ((T) -> CompletionStage<R>).asyncMemorizer(map: RMap<T, R>): AsyncRedissonMemorizer<T, R> =
    AsyncRedissonMemorizer(map, this)

/**
 * [evaluator] 결과를 Redis에 저장하도록 합니다.
 *
 * @property map 수행결과를 저장할 Redisson [RMap] 인스턴스
 * @property evaluator 수행할 비동기 함수
 */
class AsyncRedissonMemorizer<T: Any, R: Any>(
    val map: RMap<T, R>,
    val evaluator: (T) -> CompletionStage<R>,
): AsyncMemorizer<T, R> {

    companion object: KLogging()

    override fun invoke(key: T): CompletableFuture<R> {
        return map
            .containsKeyAsync(key)
            .flatMap { exist ->
                if (exist) {
                    map.getAsync(key)
                } else {
                    evaluator(key).map { value -> value.apply { map.fastPutIfAbsentAsync(key, value) } }
                }
            }
            .toCompletableFuture()
    }

    override fun clear() {
        log.debug { "Clear all memorized values. map=${map.name}" }
        map.clear()
    }
}
