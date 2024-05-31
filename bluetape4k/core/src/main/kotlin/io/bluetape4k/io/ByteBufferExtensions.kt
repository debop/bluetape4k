package io.bluetape4k.io

import io.bluetape4k.support.assertZeroOrPositiveNumber
import org.apache.commons.codec.binary.Hex
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8

/**
 * [ByteBuffer]를 읽어 바이트 배열을 빌드합니다
 */
fun ByteBuffer.getBytes(): ByteArray {
    val length = remaining()
    return if (hasArray()) {
        val offset = arrayOffset() - position()
        if (offset == 0 && length == array().size) {
            array()
        } else {
            array().copyOfRange(offset, offset + length)
        }
    } else {
        ByteArray(length).apply { this@getBytes.get(this) }
    }
}

/**
 * 대상 [ByteBuffer]를 건드리지 않고, 내용만 추출합니다.
 */
fun ByteBuffer.extractBytes(): ByteArray = ByteArray(remaining()).apply { this@extractBytes.get(this) }


/**
 * [java.nio.ByteBuffer]의 모든 내용을 읽어 [ByteArray]로 반환합니다.
 */
fun ByteBuffer.getAllBytes(): ByteArray {
    return if (hasArray()) {
        array()
    } else {
        ByteArray(remaining()).apply { this@getAllBytes.get(this) }
    }
}

/**
 * [java.nio.ByteBuffer]의 내용을 Hex 형식 인코딩 방식을 이용하여 문자열로 변환합니다.
 */
fun ByteBuffer.encodeHexString(): String = Hex.encodeHexString(this)

/**
 * Hex 형식으로 인코딩된 문자열을 [java.nio.ByteBuffer]로 변환합니다.
 */
fun String.decodeHexByteBuffer(): ByteBuffer = Hex.decodeHex(this).toByteBuffer()


/**
 * [java.nio.ByteBuffer]의 내용을 `value` 값으로 설정합니다.
 */
fun ByteBuffer.erase(value: Byte = 0) {
    if (!isReadOnly) {
        while (hasRemaining()) {
            put(value)
        }
    }
}

/**
 * 현 [ByteBuffer] 내용을 읽어 대상 [ByteBuffer] 에 씁니다
 */
@Deprecated("use moveTo", replaceWith = ReplaceWith("moveTo(dst, limit)"))
fun ByteBuffer.putTo(dst: ByteBuffer, limit: Int = Int.MAX_VALUE): Int {
    limit.assertZeroOrPositiveNumber("limit")

    val size = minOf(limit, remaining(), dst.remaining())
    if (size == remaining()) {
        dst.put(this)
    } else {
        val l = limit()
        limit(position() + size)
        dst.put(this)
        limit(l)
    }
    return size
}

/**
 * 현 [ByteBuffer] 내용을 읽어 대상 [ByteBuffer] 에 씁니다
 */
fun ByteBuffer.moveTo(dst: ByteBuffer, limit: Int = Int.MAX_VALUE): Int {
    limit.assertZeroOrPositiveNumber("limit")

    val size = minOf(limit, remaining(), dst.remaining())
    if (size == remaining()) {
        dst.put(this)
    } else {
        val l = limit()
        limit(position() + size)
        dst.put(this)
        limit(l)
    }
    return size
}

/**
 * [java.nio.ByteBuffer]를 읽어 문자열로 빌드합니다
 */
fun ByteBuffer.getString(cs: Charset = UTF_8): String = cs.decode(this).toString()

/**
 * Moves all bytes in `this` buffer to a newly created buffer with the optionally specified [size]
 */
@JvmOverloads
fun ByteBuffer.copy(size: Int = remaining()): ByteBuffer {
    return ByteBuffer.allocate(size).apply {
        this@copy.slice().moveTo(this@apply)
        clear()
    }
}

/**
 * [java.nio.ByteBuffer]를 읽어 새로운 ByteBuffer를 빌드합니다.
 */
fun ByteArray.toByteBuffer(): ByteBuffer = ByteBuffer.wrap(this)

/**
 * [ByteArray]를 읽어 새로운 direct [java.nio.ByteBuffer]를 빌드합니다.
 */
fun ByteArray.toByteBufferDirect(): ByteBuffer {
    return ByteBuffer.allocateDirect(this.size).also {
            it.put(this@toByteBufferDirect)
            it.flip()
        }
}
