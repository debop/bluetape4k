package io.bluetape4k.io.http.hc5.async.methods

import org.apache.hc.client5.http.async.methods.SimpleBody
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer
import org.apache.hc.core5.http.Header
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.Method

inline fun simpleHttpRequest(
    method: String,
    initializer: SimpleRequestBuilder.() -> Unit,
): SimpleHttpRequest {
    return SimpleRequestBuilder.create(method)
        .apply(initializer)
        .build()
}

inline fun simpleHttpRequest(
    method: Method,
    initializer: SimpleRequestBuilder.() -> Unit,
): SimpleHttpRequest {
    return SimpleRequestBuilder.create(method)
        .apply(initializer)
        .build()
}

fun simpleHttpRequestOf(
    method: String,
    host: HttpHost,
    path: String,
    body: SimpleBody? = null,
    headers: Iterable<Header>? = null,
): SimpleHttpRequest = simpleHttpRequest(method) {
    setHttpHost(host)
    setPath(path)
    body?.run { setBody(body) }
    headers?.run { setHeaders(headers.iterator()) }
}

fun simpleHttpRequestOf(
    method: Method,
    host: HttpHost,
    path: String,
    body: SimpleBody? = null,
    headers: Iterable<Header>? = null,
): SimpleHttpRequest = simpleHttpRequest(method) {
    setHttpHost(host)
    setPath(path)
    body?.run { setBody(body) }
    headers?.run { setHeaders(headers.iterator()) }
}

fun SimpleHttpRequest.toProducer(): SimpleRequestProducer =
    simpleRequestProducerOf(this)
