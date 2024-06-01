package io.bluetape4k.http.hc5.reactor

import org.apache.hc.core5.reactor.IOReactorConfig

inline fun ioReactorConfig(
    initializer: IOReactorConfig.Builder.() -> Unit,
): IOReactorConfig {
    return IOReactorConfig.custom().apply(initializer).build()
}
