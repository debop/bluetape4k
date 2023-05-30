package io.bluetape4k.data.redis.spring.serializer

import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * [RedisSerializationContext]를 빌드합니다.
 *
 * ```kotlin
 *     @Bean
 *     fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<*, *> {
 *         val context = redisSerializationContext<ByteArray, ByteArray>(RedisSerializer.byteArray()) {
 *             key(RedisSerializer.byteArray())
 *             hashKey(RedisSerializer.byteArray())
 *             value(RedisBinarySerializers.LZ4)
 *             hashValue(RedisBinarySerializers.LZ4)
 *             string(RedisSerializer.string())
 *         }
 *
 *         return ReactiveRedisTemplate(factory, context)
 *     }
 * ```
 *
 * @param K Key type
 * @param V Value type
 * @param defaultSerializer  default serializer
 * @param initializer  [RedisSerializationContext.RedisSerializationContextBuilder] initializer
 * @receiver
 * @return [RedisSerializationContext] instance
 */
inline fun <K: Any, V: Any> redisSerializationContext(
    defaultSerializer: RedisSerializer<*>? = null,
    @BuilderInference initializer: RedisSerializationContext.RedisSerializationContextBuilder<K, V>.() -> Unit,
): RedisSerializationContext<K, V> {
    val context = defaultSerializer?.let {
        RedisSerializationContext.newSerializationContext<K, V>(it)
    } ?: RedisSerializationContext.newSerializationContext<K, V>()

    return context.apply(initializer).build()
}

fun <K: Any, V: Any> redisSerializationContextOf(
    keySerializer: RedisSerializer<K>,
    valueSerializer: RedisSerializer<V>,
    defaultSerializer: RedisSerializer<*>? = null,
): RedisSerializationContext<K, V> = redisSerializationContext(defaultSerializer) {
    key(keySerializer)
    value(valueSerializer)
    hashKey(keySerializer)
    hashValue(valueSerializer)
}

fun <V: Any> redisSerializationContextOf(
    valueSerializer: RedisSerializer<V>,
    defaultSerializer: RedisSerializer<*>? = null,
): RedisSerializationContext<String, V> = redisSerializationContext(defaultSerializer) {
    key(StringRedisSerializer.UTF_8)
    value(valueSerializer)
    hashKey(StringRedisSerializer.UTF_8)
    hashValue(valueSerializer)
}
