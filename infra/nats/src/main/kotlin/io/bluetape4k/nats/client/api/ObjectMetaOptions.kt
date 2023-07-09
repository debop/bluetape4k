package io.bluetape4k.nats.client.api

import io.nats.client.api.ObjectMetaOptions

inline fun objectMetaOptions(initializer: ObjectMetaOptions.Builder.() -> Unit): ObjectMetaOptions =
    ObjectMetaOptions.Builder().apply(initializer).build()
