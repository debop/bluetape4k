package io.bluetape4k.io.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

/**
 * Build [ManagedChannel]
 */
inline fun managedChannel(
    host: String,
    port: Int,
    initializer: ManagedChannelBuilder<*>.() -> Unit,
): ManagedChannel {
    return ManagedChannelBuilder
        .forAddress(host, port)
        .apply(initializer)
        .build()
}

/**
 * Build [ManagedChannel]
 */
inline fun managedChannel(
    target: String,
    initializer: ManagedChannelBuilder<*>.() -> Unit,
): ManagedChannel {
    return ManagedChannelBuilder
        .forTarget(target)
        .apply(initializer)
        .build()
}
