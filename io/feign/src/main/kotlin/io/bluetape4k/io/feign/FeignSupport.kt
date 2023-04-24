package io.bluetape4k.io.feign

import feign.Feign
import feign.Request
import feign.Target
import feign.codec.Decoder
import feign.codec.Encoder

@JvmField
internal val defaultRequestOptions = Request.Options()

inline fun feignBuilder(intializer: Feign.Builder.() -> Unit): Feign.Builder {
    return Feign.Builder().apply(intializer)
}

fun feingBuilderOf(
    client: feign.Client,
    encoder: Encoder = Encoder.Default(),
    decoder: Decoder = Decoder.Default(),
    options: Request.Options = defaultRequestOptions,
    logLevel: feign.Logger.Level = feign.Logger.Level.BASIC,
): Feign.Builder {
    return feignBuilder {
        client(client)
        encoder(encoder)
        decoder(decoder)
        options(options)
        logLevel(logLevel)
    }
}

/**
 * 지정한 [T] 타입의 Feign Client 를 생성합니다.
 *
 * 동적인 url 을 사용하기 위해 함수의 첫번째 인자를 [java.net.URI] 를 사용하고, 여기서는 baseUrl 을 지정하지 않으면 됩니다.
 *
 * ```
 * interface GitHub {
 *   // host 값을 동적 url 로 사용합니다.
 *   // issue 는 RequestBody 로 전달됩니다
 *   @RequestLine("POST /repos/{owner}/{repo}/issues")
 *   fun createIssue(
 *      host:URI,
 *      issue: Issue,
 *      @Param("owner") owner String,
 *      @Param("repo") String repo
 *   ): Unit
 * }
 *
 * // Feign Client 를 생성합니다.
 * val client:GitHub = feignBuilderOf(client=ApacheHttp5C).target<GitHub>()
 * ```
 * @param T
 * @param baseUrl base url for service
 * @return Service API instance
 */
inline fun <reified T: Any> Feign.Builder.target(baseUrl: String? = null): T = when {
    baseUrl.isNullOrBlank() -> target(Target.EmptyTarget.create(T::class.java))
    else -> target(T::class.java, baseUrl)
}
