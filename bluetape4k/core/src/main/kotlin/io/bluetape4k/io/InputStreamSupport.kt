package io.bluetape4k.io

import io.bluetape4k.support.assertPositiveNumber
import io.bluetape4k.support.assertZeroOrPositiveNumber
import io.bluetape4k.support.toUtf8String
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.StringWriter
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

/**
 * [InputStream]을 읽어 [Writer]에 씁니다.
 *
 * @param out         데이터를 쓸 대상 [Writer]
 * @param cs          Charset
 * @param bufferSize  buffer size
 * @return 복사한 데이터의 Byte 크기
 */
fun InputStream.copyTo(out: Writer, cs: Charset = UTF_8, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long =
    this.reader(cs).buffered().copyTo(out, bufferSize)


/**
 * [InputStream]을 읽어 [OutputStream]에 씁니다.
 *
 * @param out         데이터를 쓸 대상 [OutputStream]
 * @param bufferSize  buffer size
 * @return 복사한 데이터의 Byte 크기
 */
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

/**
 * [ReadableByteChannel]을 읽어 [WritableByteChannel]에 씁니다.
 *
 * @param bufferSize buffer size
 * @return 복사한 데이터의 Byte 크기
 */
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

/**
 * [Reader]를 읽어 [OutputStream]에 씁니다.
 *
 * @param out         데이터를 쓸 대상 [Writer]
 * @param bufferSize  buffer size
 * @return 복사한 데이터의 Byte 크기
 */
fun Reader.copyTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE, cs: Charset = UTF_8): Long {
    bufferSize.assertPositiveNumber("bufferSize")

    OutputStreamWriter(out, cs).buffered().use { writer ->
        val count = copyTo(writer, bufferSize)
        out.flush()
        return count
    }
}

/**
 * [Reader]를 읽어 문자열로 반환합니다.
 *
 * @param out         데이터를 쓸 대상 [Writer]
 * @param bufferSize  buffer size
 * @return 읽어들인 문자열
 */
fun Reader.copyToString(bufferSize: Int = DEFAULT_BUFFER_SIZE): String {
    return StringWriter(bufferSize).use { writer ->
        this.copyTo(writer)
        writer.toString()
    }
}

/**
 * [ByteArray]를 읽어들이는 [InputStream]을 빌드합니다.
 */
fun ByteArray.toInputStream(): InputStream = ByteArrayInputStream(this).buffered()

/**
 * [String]를 읽어들이는 [InputStream]을 빌드합니다.
 */
fun String.toInputStream(cs: Charset = UTF_8): InputStream = toByteArray(cs).toInputStream()

/**
 * [InputStream]를 읽어 [ByteArrayOutputStream]에 씁니다.
 */
fun InputStream.toOutputStream(blockSize: Int = DEFAULT_BLOCK_SIZE): ByteArrayOutputStream =
    ByteArrayOutputStream(DEFAULT_BUFFER_SIZE).apply {
        this@toOutputStream.copyTo(this, blockSize)
    }

/**
 * [ByteArray]를 읽어 [ByteArrayOutputStream]에 씁니다.
 */
fun ByteArray.toOutputStream(blockSize: Int = DEFAULT_BLOCK_SIZE): ByteArrayOutputStream =
    toInputStream().toOutputStream(blockSize)

/**
 * [String]를 읽어 [ByteArrayOutputStream]에 씁니다.
 */
fun String.toOutputStream(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): ByteArrayOutputStream =
    toByteArray(cs).toOutputStream(blockSize)

/**
 * [InputStream]의 available한 부분을 읽어 [ByteArray]로 반환합니다.
 */
fun InputStream.availableBytes(): ByteArray = ByteArray(available()).also { read(it) }

/**
 * [InputStream]을 읽어 [ByteArray]로 반환합니다.
 */
fun InputStream.toByteArray(blockSize: Int = DEFAULT_BLOCK_SIZE): ByteArray =
    toOutputStream(blockSize).use { it.toByteArray() }

/**
 * [InputStream]을 읽어 [CharArray]로 반환합니다.
 */
fun InputStream.toCharArray(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): CharArray =
    reader(cs).buffered(blockSize).use { it.readText().toCharArray() }

/**
 * [InputStream]을 읽어 [ByteBuffer]로 반환합니다.
 */
fun InputStream.toByteBuffer(blockSize: Int = DEFAULT_BLOCK_SIZE): ByteBuffer =
    ByteBuffer.wrap(this.toByteArray(blockSize))

/**
 * [InputStream]을 읽어 문자열로 반환합니다.
 */
fun InputStream.toString(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): String =
    toByteArray(blockSize).toString(cs)

/**
 * [InputStream]을 읽어 UTF-8 문자열로 반환합니다.
 */
fun InputStream.toUtf8String(blockSize: Int = DEFAULT_BLOCK_SIZE): String = toByteArray(blockSize).toUtf8String()

/**
 * [InputStream]을 읽어 문자열 컬렉션으로 반환합니다.
 */
fun InputStream.toStringList(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): List<String> =
    reader(cs).buffered(blockSize).useLines { it.toList() }

/**
 * [InputStream]을 읽어 UTF-8 문자열 컬렉션으로 반환합니다.
 */
fun InputStream.toUtf8StringList(blockSize: Int = DEFAULT_BLOCK_SIZE): List<String> =
    reader(UTF_8).buffered(blockSize).useLines { it.toList() }

/**
 * [InputStream]을 읽어 라인 시퀀스로 반환합니다.
 */
fun InputStream.toLineSequence(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): Sequence<String> =
    reader(cs).buffered(blockSize).lineSequence()

/**
 * [InputStream]을 읽어 UTF-8 라인 시퀀스로 반환합니다.
 */
fun InputStream.toUtf8LineSequence(blockSize: Int = DEFAULT_BLOCK_SIZE): Sequence<String> =
    reader(UTF_8).buffered(blockSize).lineSequence()

/**
 * [ByteArray]를 읽어 문자열 컬렉션으로 변홥합니다.
 */
fun ByteArray.toStringList(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): List<String> =
    toInputStream().toStringList(cs, blockSize)

/**
 * [ByteArray]를 읽어 UTF-8 문자열 컬렉션으로 변홥합니다.
 */
fun ByteArray.toUtf8StringList(blockSize: Int = DEFAULT_BLOCK_SIZE): List<String> =
    toInputStream().toUtf8StringList(blockSize)

/**
 * [ByteArray]를 읽어 라인 시퀀스로 변환합니다.
 */
fun ByteArray.toLineSequence(cs: Charset = UTF_8, blockSize: Int = DEFAULT_BLOCK_SIZE): Sequence<String> =
    toInputStream().toLineSequence(cs, blockSize)

/**
 * [ByteArray]를 읽어 UTF-8 라인 시퀀스로 변환합니다.
 */
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
