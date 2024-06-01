package io.bluetape4k.http.hc5.async.methods

import org.apache.hc.client5.http.async.methods.ConfigurableHttpRequest
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.net.URIAuthority

fun configurableHttpRequestOf(
    method: String,
    host: HttpHost,
    path: String,
): ConfigurableHttpRequest {
    return ConfigurableHttpRequest(method, host, path)
}

fun configurableHttpRequestOf(
    method: String,
    path: String,
    scheme: String? = null,
    authority: URIAuthority? = null,
): ConfigurableHttpRequest {
    return ConfigurableHttpRequest(method, scheme, authority, path)
}
