package io.bluetape4k.io.http.hc5.http

import org.apache.hc.core5.http.Header
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.Method
import org.apache.hc.core5.http.message.BasicHttpRequest
import org.apache.hc.core5.http.support.BasicRequestBuilder

inline fun basicHttpRequest(
    method: String,
    initializer: BasicRequestBuilder.() -> Unit,
): BasicHttpRequest {
    return BasicRequestBuilder.create(method).apply(initializer).build()
}

inline fun basicHttpRequest(
    method: Method,
    initializer: BasicRequestBuilder.() -> Unit,
): BasicHttpRequest = basicHttpRequest(method.name, initializer)

fun basicHttpRequestOf(
    method: String,
    host: HttpHost,
    path: String,
    headers: Iterable<Header>? = null,
): BasicHttpRequest = basicHttpRequest(method) {
    setHttpHost(host)
    setPath(path)
    headers?.run { setHeaders(headers.iterator()) }
}

fun basicHttpRequestOf(
    method: Method,
    host: HttpHost,
    path: String,
    headers: Iterable<Header>? = null,
): BasicHttpRequest = basicHttpRequest(method) {
    setHttpHost(host)
    setPath(path)
    headers?.run { setHeaders(headers.iterator()) }
}
