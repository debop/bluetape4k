package io.bluetape4k.io.http.hc5.http

import org.apache.hc.core5.http.config.Http1Config

inline fun http1Config(initializer: Http1Config.Builder.() -> Unit): Http1Config {
    return Http1Config.custom().apply(initializer).build()
}

inline fun http1Config(
    source: Http1Config,
    initlializer: Http1Config.Builder.() -> Unit,
): Http1Config {
    return Http1Config.copy(source).apply(initlializer).build()
}

fun http1ConfigOf(): Http1Config = Http1Config.DEFAULT
