package io.bluetape4k.nats.client

import io.nats.client.JetStreamManagement

fun JetStreamManagement.deleteStreamIfExists(stream: String) {
    runCatching { deleteStream(stream) }
}
