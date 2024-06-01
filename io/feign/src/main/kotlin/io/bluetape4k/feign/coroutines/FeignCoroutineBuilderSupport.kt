package io.bluetape4k.feign.coroutines

import feign.AsyncClient
import feign.Request
import feign.Target
import feign.codec.Decoder
import feign.codec.Encoder
import feign.kotlin.CoroutineFeign
import io.bluetape4k.feign.defaultRequestOptions

/**
 * Coroutine 용 Feign Builder 를 생성합니다.
 *
 * @param C Context Type
 * @param intializer CoroutineFeign.CoroutineBuilder 초기화 블럭
 * @receiver CoroutineFeign.CoroutineBuilder
 * @return [CoroutineFeign.CoroutineBuilder] instance
 */
inline fun <C: Any> coroutineFeignBuilder(
    intializer: CoroutineFeign.CoroutineBuilder<C>.() -> Unit,
): CoroutineFeign.CoroutineBuilder<C> {
    return CoroutineFeign.CoroutineBuilder<C>().apply(intializer)
}

fun <C: Any> coroutineFeignBuilderOf(
    asyncClient: AsyncClient<C>, // = AsyncClient.Default(null, ForkJoinPool.commonPool()),
    encoder: Encoder = Encoder.Default(),
    decoder: Decoder = Decoder.Default(),
    opptions: Request.Options = defaultRequestOptions,
    logLevel: feign.Logger.Level = feign.Logger.Level.BASIC,
): CoroutineFeign.CoroutineBuilder<C> {
    return coroutineFeignBuilder {
        client(asyncClient)
        encoder(encoder)
        decoder(decoder)
        options(opptions)
        logLevel(logLevel)
    }
}

/**
 * Feign 용 Client 를 생성합니다.
 *
 * @param T Client type
 * @param baseUrl Base URL
 * @return Feign Client instance
 */
inline fun <reified T: Any> CoroutineFeign.CoroutineBuilder<*>.client(baseUrl: String? = null): T = when {
    baseUrl.isNullOrBlank() -> target(Target.EmptyTarget.create(T::class.java))
    else                    -> target(T::class.java, baseUrl)
}
