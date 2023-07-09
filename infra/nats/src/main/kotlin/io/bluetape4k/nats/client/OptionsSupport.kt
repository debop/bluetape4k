package io.bluetape4k.nats.client

import io.nats.client.Options

inline fun options(initializer: Options.Builder.() -> Unit): Options {
    return Options.Builder().apply(initializer).build()
}

fun optionsOf(
    url: String = Options.DEFAULT_URL,
    maxReconnects: Int = Options.DEFAULT_MAX_RECONNECT,
    bufferSize: Int = Options.DEFAULT_BUFFER_SIZE,
): Options = options {
    this.server(url)
    this.maxReconnects(maxReconnects)
    this.bufferSize(bufferSize)
}
