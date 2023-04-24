package io.bluetape4k.io.feign.coroutines

import feign.AsyncClient
import feign.Request
import feign.Target
import feign.codec.Decoder
import feign.codec.Encoder
import feign.kotlin.CoroutineFeign
import io.bluetape4k.io.feign.defaultRequestOptions


inline fun <C: Any> coroutineFeignBuilder(
    intializer: CoroutineFeign.CoroutineBuilder<C>.() -> Unit,
): CoroutineFeign.CoroutineBuilder<C> {
    return CoroutineFeign.CoroutineBuilder<C>().apply(intializer)
}

fun <C: Any> coroutineFeignBuilderOf(
    asyncClient: AsyncClient<C> = AsyncClient.Default(null, null),
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

inline fun <reified T: Any> CoroutineFeign.CoroutineBuilder<out Any>.target(baseUrl: String? = null): T = when {
    baseUrl.isNullOrBlank() -> target(Target.EmptyTarget.create(T::class.java))
    else -> target(T::class.java, baseUrl)
}
