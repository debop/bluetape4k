package io.bluetape4k.http.hc5.protocol

import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.core5.http.protocol.HttpContext

fun HttpContext.adapt(): HttpClientContext = HttpClientContext.adapt(this)

fun httpClientContextOf(): HttpClientContext = HttpClientContext.create()

fun httpClientContextOf(context: HttpContext): HttpClientContext = HttpClientContext(context)
