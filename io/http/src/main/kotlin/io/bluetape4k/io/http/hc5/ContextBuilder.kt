package io.bluetape4k.io.http.hc5

import org.apache.hc.client5.http.ContextBuilder
import org.apache.hc.client5.http.SchemePortResolver
import org.apache.hc.client5.http.protocol.HttpClientContext

inline fun httpClientContext(initializer: ContextBuilder.() -> Unit): HttpClientContext {
    return ContextBuilder.create().apply(initializer).build()
}

fun contextBuilderOf(): ContextBuilder = ContextBuilder.create()

fun contextBuilderOf(schemePortResolver: SchemePortResolver): ContextBuilder =
    ContextBuilder.create(schemePortResolver)
