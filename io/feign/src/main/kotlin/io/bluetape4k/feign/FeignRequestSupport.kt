package io.bluetape4k.feign

import feign.Request
import feign.Request.HttpMethod
import feign.Request.Options
import feign.RequestTemplate
import java.nio.charset.Charset

@JvmField
val defaultRequestOptions: Request.Options = Options()

inline fun requestOptions(intializer: Options.() -> Unit): Options {
    return Options().apply(intializer)
}

fun feignRequestOf(
    url: String,
    httpMetho: HttpMethod = HttpMethod.GET,
    headers: Map<String, Collection<String>> = emptyMap(),
    body: ByteArray? = null,
    charset: Charset = Charsets.UTF_8,
    requestTemplate: RequestTemplate? = null,
): feign.Request {
    return Request.create(httpMetho, url, headers, body, charset, requestTemplate)
}
