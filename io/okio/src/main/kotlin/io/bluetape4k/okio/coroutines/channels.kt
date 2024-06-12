package io.bluetape4k.okio.coroutines

import io.bluetape4k.okio.coroutines.internal.SEGMENT_SIZE
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.Buffer
import okio.Timeout
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun AsynchronousSocketChannel.asAsyncSource(): AsyncSource {
    val channel = this

    return object: AsyncSource {
        val buffer = ByteBuffer.allocateDirect(SEGMENT_SIZE.toInt())

        override suspend fun read(sink: Buffer, byteCount: Long): Long {
            buffer.clear()
            buffer.limit(minOf(SEGMENT_SIZE, byteCount).toInt())
            val read = channel.aRead(buffer)
            buffer.flip()
            if (read > 0) sink.write(buffer)
            return read.toLong()
        }

        override suspend fun close() {
            channel.close()
        }

        override suspend fun timeout(): Timeout {
            return Timeout.NONE
        }
    }
}

suspend fun AsynchronousSocketChannel.asAsyncSink(): AsyncSink {
    val channel = this

    return object: AsyncSink {
        val cursor = Buffer.UnsafeCursor()

        override suspend fun write(source: Buffer, byteCount: Long) {
            source.readUnsafe()
        }

        override suspend fun flush() {
            // Nothing to do
        }

        override suspend fun close() {
            channel.close()
        }

        override suspend fun timeout(): Timeout {
            return Timeout.NONE
        }
    }
}


private suspend fun AsynchronousSocketChannel.aRead(buffer: ByteBuffer): Int = suspendCancellableCoroutine { cont ->
    read(buffer, cont, ChannelCompletionHandler)
    cont.invokeOnCancellation { close() }
}

private suspend fun AsynchronousSocketChannel.aWrite(buffer: ByteBuffer): Int = suspendCancellableCoroutine { cont ->
    write(buffer, cont, ChannelCompletionHandler)
    cont.invokeOnCancellation { close() }
}

private suspend fun AsynchronousFileChannel.aRead(buffer: ByteBuffer, position: Long): Int =
    suspendCancellableCoroutine { cont ->
        read(buffer, position, cont, ChannelCompletionHandler)
        cont.invokeOnCancellation { close() }
    }

private suspend fun AsynchronousFileChannel.aWrite(buffer: ByteBuffer, position: Long): Int =
    suspendCancellableCoroutine { cont ->
        write(buffer, position, cont, ChannelCompletionHandler)
        cont.invokeOnCancellation { close() }
    }

private object ChannelCompletionHandler: CompletionHandler<Int, CancellableContinuation<Int>> {
    override fun completed(result: Int, attachment: CancellableContinuation<Int>) {
        attachment.resume(result)
    }

    override fun failed(exc: Throwable, attachment: CancellableContinuation<Int>) {
        attachment.resumeWithException(exc)
    }
}
