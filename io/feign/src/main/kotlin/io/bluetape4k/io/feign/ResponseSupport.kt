package io.bluetape4k.io.feign

import feign.Response
import java.io.Reader

inline fun responseBuilder(initializer: feign.Response.Builder.() -> Unit): feign.Response.Builder {
    return feign.Response.builder().apply(initializer)
}

inline fun response(initializer: feign.Response.Builder.() -> Unit): feign.Response {
    return feign.Response.builder().apply(initializer).build()
}

fun Response.isJsonBody(): Boolean {
    val contentType = headers()["content-type"]
    return contentType?.any { it.contains("application/json", true) } ?: false
}

fun Response.bodyAsReader(): Reader {
    return body().asReader(charset())
}
