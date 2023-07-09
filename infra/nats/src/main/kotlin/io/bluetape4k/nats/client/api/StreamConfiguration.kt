package io.bluetape4k.nats.client.api

import io.nats.client.api.StreamConfiguration

inline fun streamConfiguration(initializer: StreamConfiguration.Builder.() -> Unit): StreamConfiguration {
    return StreamConfiguration.Builder().apply(initializer).build()
}
