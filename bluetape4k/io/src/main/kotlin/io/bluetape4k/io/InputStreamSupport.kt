package io.bluetape4k.io

import io.bluetape4k.core.assertPositiveNumber
import io.bluetape4k.core.assertZeroOrPositiveNumber
import io.bluetape4k.support.toUtf8String
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.Writer
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8

@JvmField
val emptyInputStream = ByteArrayInputStream(ByteArray(0))

@JvmField
val emptyOutputStream = ByteArrayOutputStream(0)

const val DEFAULT_BUFFER_SIZE = 8192
const val DEFAULT_BLOCK_SIZE = 4096
const val MINIMAL_BLOCK_SIZE = 512


fun InputStream.copyTo(out: Writer, cs: Charset = UTF_8, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long =
    this.reader(cs).copyTo(out, bufferSize)


fun InputStream.copyTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
    bufferSize.assertPositiveNumber("bufferSize")

    var readBytes = 0L
    val buffer = ByteArray(bufferSize)
    do {
        val readCount = this.read(buffer)
        if (readCount > 0) {
            out.write(buffer, 0, readCount)
            readBytes += readCount
        }
    } while (readCount > 0)

    return readBytes
}


fun ReadableByteChannel.copyTo(out: WritableByteChannel, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
    bufferSize.assertPositiveNumber("bufferSize")

    var readBytes = 0L
    val buffer = ByteBuffer.allocateDirect(bufferSize)

    while (this.read(buffer) > 0) {
        buffer.flip()
        readBytes += out.write(buffer)
        buffer.compact()
    }
    return readBytes
}


fun Reader.copyTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE, cs: Charset = UTF_8): Long {
    bufferSize.assertPositiveNumber("bufferSize")

    OutputStreamWriter(out, cs).use { writer ->
        val count = copyTo(writer, bufferSize)
        out.flush()
        return count
    }
}

fun ByteArray.toInputStream(): InputStream = ByteArrayInputStream(this).buffered()


fun String.toInputStream(cs: Charset = UTF_8): InputStream =
    toByteArray(cs).toInputStream()


fun InputStream.toOutputStream(blockSize: Int = DEFAULT_BLOCK_SIZE): ByteArrayOutputStream =
    ByteArrayOutputStream().apply {
        this@toOutputStream.copyTo(this, blockSize)
    }


fun ByteArray.toOutputStream(blockSize: Int = DEFAULT_BLOCK_SIZE): ByteArrayOutputStream =
    toInputStream().toOutputStream(blockSize)


fun String.toOutputStream(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): ByteArrayOutputStream =
    toByteArray(cs).toOutputStream(blockSize)

fun InputStream.availableBytes(): ByteArray = ByteArray(available()).also { read(it) }


fun InputStream.toByteArray(blockSize: Int = DEFAULT_BLOCK_SIZE): ByteArray =
    toOutputStream(blockSize).use { it.toByteArray() }


fun InputStream.toByteBuffer(blockSize: Int = DEFAULT_BLOCK_SIZE): ByteBuffer =
    ByteBuffer.wrap(this.toByteArray(blockSize))


fun InputStream.toString(cs: Charset = UTF_8): String = toByteArray().toString(cs)


fun InputStream.toUtf8String(blockSize: Int = DEFAULT_BLOCK_SIZE): String =
    toByteArray(blockSize).toUtf8String()

fun InputStream.toStringList(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): List<String> =
    reader(cs).buffered(blockSize).useLines { it.toList() }

fun InputStream.toUtf8StringList(blockSize: Int = DEFAULT_BLOCK_SIZE): List<String> =
    reader(UTF_8).buffered(blockSize).useLines { it.toList() }


fun InputStream.toLineSequence(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): Sequence<String> =
    reader(cs).buffered(blockSize).lineSequence()


fun InputStream.toUtf8LineSequence(blockSize: Int = DEFAULT_BLOCK_SIZE): Sequence<String> =
    reader(UTF_8).buffered(blockSize).lineSequence()


fun ByteArray.toStringList(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): List<String> =
    toInputStream().toStringList(cs, blockSize)


fun ByteArray.toUtf8StringList(blockSize: Int = DEFAULT_BLOCK_SIZE): List<String> =
    toInputStream().toUtf8StringList(blockSize)


fun ByteArray.toLineSequence(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): Sequence<String> =
    toInputStream().toLineSequence(cs, blockSize)


fun ByteArray.toUtf8LineSequence(blockSize: Int = DEFAULT_BLOCK_SIZE): Sequence<String> =
    toInputStream().toUtf8LineSequence(blockSize)

/**
 * InputStream 정보를 읽어 `dst`에 씁니다.
 * @receiver InputStream
 * @param dst ByteBuffer
 * @param limit Int
 * @return Int
 */
fun InputStream.putTo(dst: ByteBuffer, limit: Int = dst.remaining()): Int {
    limit.assertZeroOrPositiveNumber("limit")

    return if (dst.hasArray()) {
        val readCount = read(dst.array(), dst.arrayOffset() + dst.position(), limit)
        if (readCount > 0) {
            dst.position(dst.position() + readCount)
        }
        readCount
    } else {
        val array = ByteArray(minOf(available(), limit))
        val readCount = read(array)

        if (readCount > 0) {
            dst.put(array)
        }
        readCount
    }
}
