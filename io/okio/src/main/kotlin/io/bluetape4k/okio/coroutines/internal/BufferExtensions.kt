package io.bluetape4k.okio.coroutines.internal

import okio.Buffer
import okio.Timeout

internal inline fun <R> Buffer.readUnsafe(
    cursor: Buffer.UnsafeCursor = Buffer.UnsafeCursor(),
    block: (cursor: Buffer.UnsafeCursor) -> R,
): R {
    return readUnsafe(cursor).use {
        block(it)
    }
}

internal inline fun <R> Buffer.readAndWriteUnsafe(
    cursor: Buffer.UnsafeCursor = Buffer.UnsafeCursor(),
    block: (cursor: Buffer.UnsafeCursor) -> R,
): R {
    return readAndWriteUnsafe(cursor).use {
        block(it)
    }
}

// TODO: refactoring to Timeout.run
internal suspend inline fun <R> withTimeout(timeout: Timeout, crossinline block: suspend () -> R): R {
    if (timeout.timeoutNanos() == 0L && !timeout.hasDeadline()) {
        return block()
    }

    val now = System.nanoTime()
    val waitNanos = when {
        timeout.timeoutNanos() != 0L && timeout.hasDeadline() -> minOf(
            timeout.timeoutNanos(),
            timeout.deadlineNanoTime() - now
        )

        timeout.timeoutNanos() != 0L                          -> timeout.timeoutNanos()
        timeout.hasDeadline()                                 -> timeout.deadlineNanoTime() - now
        else                                                  -> throw AssertionError()
    }

    return kotlinx.coroutines.withTimeout((waitNanos / 1_000_000f).toLong()) {
        block()
    }
}
