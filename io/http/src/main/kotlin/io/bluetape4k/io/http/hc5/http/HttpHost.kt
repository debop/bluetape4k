package io.bluetape4k.io.http.hc5.http

import org.apache.hc.core5.http.HttpHost
import java.net.URI

fun URI.toHttpHost(): HttpHost = HttpHost.create(this)

fun httpHostOf(url: String): HttpHost = HttpHost.create(url)
