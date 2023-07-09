package io.bluetape4k.nats.client.api

import io.nats.client.api.StreamInfoOptions

inline fun StreamInfoOptions(
    initializer: StreamInfoOptions.Builder.() -> Unit,
): StreamInfoOptions =
    StreamInfoOptions.builder().apply(initializer).build()
