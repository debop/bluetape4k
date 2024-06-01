package io.bluetape4k.http.hc5.http

import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.core5.http.HttpHost

fun authScopeOf(
    protocol: String,
    host: String,
    port: Int = -1,
    realm: String? = null,
    schemeName: String? = null,
): AuthScope = AuthScope(protocol, host, port, realm, schemeName)

fun authScopeOf(
    origin: HttpHost,
    realm: String? = null,
    schemeName: String? = null,
): AuthScope = AuthScope(origin, realm, schemeName)

fun authScopeOf(
    url: String,
    realm: String? = null,
    schemeName: String? = null,
): AuthScope = AuthScope(httpHostOf(url), realm, schemeName)

fun authScopeOf(
    host: String,
    port: Int,
): AuthScope = AuthScope(host, port)
