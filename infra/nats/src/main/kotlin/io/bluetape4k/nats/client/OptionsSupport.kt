package io.bluetape4k.nats.client

import io.nats.client.Options

inline fun natsOptions(initializer: Options.Builder.() -> Unit): Options {
    return Options.builder().apply(initializer).build()
}

fun natsOptionsOf(
    url: String = Options.DEFAULT_URL,
    maxReconnects: Int = Options.DEFAULT_MAX_RECONNECT,
    bufferSize: Int = Options.DEFAULT_BUFFER_SIZE,
): Options = natsOptions {
    this.server(url)
    this.maxReconnects(maxReconnects)
    this.bufferSize(bufferSize)
}
