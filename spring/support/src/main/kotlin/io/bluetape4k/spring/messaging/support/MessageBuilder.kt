package io.bluetape4k.spring.messaging.support

import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder


inline fun <T: Any> message(
    payload: T,
    initializer: MessageBuilder<T>.() -> Unit = {},
): Message<T> {
    return MessageBuilder.withPayload(payload).apply(initializer).build()
}

fun <T: Any> messageOf(payload: T): Message<T> = message(payload)

fun <T: Any> messageOf(payload: T, headers: Map<String, Any?>): Message<T> =
    message(payload) {
        headers.forEach { (name, value) ->
            setHeader(name, value)
        }
    }
