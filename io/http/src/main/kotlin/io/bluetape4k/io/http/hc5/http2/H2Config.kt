package io.bluetape4k.io.http.hc5.http2

import org.apache.hc.core5.http2.config.H2Config

inline fun h2Config(
    initlializer: H2Config.Builder.() -> Unit,
): H2Config {
    return H2Config.custom().apply(initlializer).build()
}

inline fun h2Config(
    source: H2Config,
    initlializer: H2Config.Builder.() -> Unit,
): H2Config {
    return H2Config.copy(source).apply(initlializer).build()
}

fun h2ConfigOf(): H2Config = H2Config.DEFAULT
