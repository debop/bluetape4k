package io.bluetape4k.io.http.hc5.async.methods

import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer

fun simpleResponseConsumerOf(): SimpleResponseConsumer {
    return SimpleResponseConsumer.create()
}
