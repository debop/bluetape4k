package io.bluetape4k.nats.client

import io.bluetape4k.support.toUtf8Bytes
import io.nats.client.Connection
import io.nats.client.Message
import io.nats.client.impl.Headers
import kotlinx.coroutines.future.await
import kotlin.time.Duration
import kotlin.time.toJavaDuration

fun Connection.publish(subject: String, body: String, headers: Headers? = null) {
    publish(subject, headers, body.toUtf8Bytes())
}

fun Connection.publish(subject: String, replyTo: String, body: String, headers: Headers? = null) {
    publish(subject, replyTo, headers, body.toUtf8Bytes())
}

suspend fun Connection.requestSuspending(
    message: Message,
    timeout: Duration? = null,
): Message {
    return requestWithTimeout(message, timeout?.toJavaDuration()).await()
}

suspend fun Connection.requestSuspending(
    subject: String,
    body: ByteArray,
    headers: Headers? = null,
): Message {
    return request(subject, headers, body).await()
}


suspend fun Connection.requestWithTimeoutSuspending(
    subject: String,
    body: ByteArray,
    headers: Headers? = null,
    timeout: Duration? = null,
): Message {
    return requestWithTimeout(subject, headers, body, timeout?.toJavaDuration()).await()
}

suspend fun Connection.drainSuspending(timeout: Duration): Boolean =
    drain(timeout.toJavaDuration()).await()

fun Connection.flush(timeout: Duration) {
    flush(timeout.toJavaDuration())
}
