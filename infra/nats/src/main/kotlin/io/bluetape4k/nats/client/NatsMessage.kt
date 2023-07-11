package io.bluetape4k.nats.client

import io.bluetape4k.support.toUtf8Bytes
import io.nats.client.Message
import io.nats.client.impl.Headers
import io.nats.client.impl.NatsMessage

inline fun natsMessage(initializer: NatsMessage.Builder.() -> Unit): NatsMessage {
    return NatsMessage.builder().apply(initializer).build()
}

fun natsMessageOf(message: Message) = NatsMessage(message)

fun natsMessageOf(
    subject: String,
    replyTo: String,
    headers: Headers? = null,
    data: ByteArray,
): NatsMessage = NatsMessage(subject, replyTo, headers, data)

fun natsMessageOf(
    subject: String,
    replyTo: String,
    headers: Headers? = null,
    data: String,
): NatsMessage = NatsMessage(subject, replyTo, headers, data.toUtf8Bytes())
