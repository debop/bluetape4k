package io.bluetape4k.nats.client.api

import io.nats.client.api.ObjectMeta

inline fun objectMeta(objectName: String, initializer: ObjectMeta.Builder.() -> Unit): ObjectMeta =
    ObjectMeta.builder(objectName).apply(initializer).build()

inline fun objectMeta(om: ObjectMeta, initializer: ObjectMeta.Builder.() -> Unit): ObjectMeta =
    ObjectMeta.builder(om).apply(initializer).build()
