package io.bluetape4k.nats.client.api

import io.nats.client.api.ConsumerConfiguration

inline fun consumerConfiguration(initializer: ConsumerConfiguration.Builder.() -> Unit): ConsumerConfiguration =
    ConsumerConfiguration.Builder().apply(initializer).build()
