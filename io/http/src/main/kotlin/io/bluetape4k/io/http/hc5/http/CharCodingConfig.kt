package io.bluetape4k.io.http.hc5.http

import org.apache.hc.core5.http.config.CharCodingConfig

inline fun charCodingConfig(initializer: CharCodingConfig.Builder.() -> Unit): CharCodingConfig {
    return CharCodingConfig.custom().apply(initializer).build()
}

inline fun charCodingConfig(
    source: CharCodingConfig,
    initializer: CharCodingConfig.Builder.() -> Unit,
): CharCodingConfig {
    return CharCodingConfig.copy(source).apply(initializer).build()
}

fun charCodingConfigOf(): CharCodingConfig = CharCodingConfig.DEFAULT
