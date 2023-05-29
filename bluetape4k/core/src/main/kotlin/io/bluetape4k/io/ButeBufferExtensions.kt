package io.bluetape4k.io

import io.bluetape4k.core.assertZeroOrPositiveNumber
import org.apache.commons.codec.binary.Hex
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8

/**
 * ByteBuffer를 읽어 바이트 배열을 빌드합니다
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
fun ByteBuffer.extractBytes(): ByteArray =
    ByteArray(remaining()).apply { this@extractBytes.get(this) }


/**
 * ByteBuffer의 모든 내용을 읽어 [ByteArray]로 반환합니다.
 */
fun ByteBuffer.getAllBytes(): ByteArray {
    return if (hasArray()) {
        array()
    } else {
        ByteArray(remaining()).apply { this@getAllBytes.get(this) }
    }
}

/**
 * ByteBuffer의 내용을 Hex 형식 인코딩 방식을 이용하여 문자열로 변환합니다.
 */
fun ByteBuffer.encodeHexString(): String = Hex.encodeHexString(this)

/**
 * Hex 형식으로 인코딩된 문자열을 [ByteBuffer]로 변환합니다.
 */
fun String.decodeHexByteBuffer(): ByteBuffer = Hex.decodeHex(this).toByteBuffer()


/**
 * ByteBuffer의 내용을 모두 삭제합니다.
 */
fun ByteBuffer.erase(value: Byte = 0) {
    if (!isReadOnly) {
        while (hasRemaining()) {
            put(value)
        }
    }
}

/**
 * 현 ByteBuffer 내용을 읽어 대상 ByteBuffer 에 씁니다
 */
fun ByteBuffer.putTo(dst: ByteBuffer, limit: Int = Int.MAX_VALUE): Int {
    limit.assertZeroOrPositiveNumber("limit")

    val size = minOf(limit, remaining(), dst.remaining())
    repeat(size) {
        dst.put(get())
    }
    return size
}

/**
 * ByteBuffer를 읽어 문자열로 빌드합니다
 */
fun ByteBuffer.getString(cs: Charset = UTF_8): String = cs.decode(this).toString()

/**
 * 현 ByteBuffer의 나머지 사이즈를 읽어서, 새로운 ByteBuffer를 만든다.
 * @receiver ByteBuffer
 * @param size Int
 * @return ByteBuffer
 */
@JvmOverloads
fun ByteBuffer.copy(size: Int = remaining()): ByteBuffer {
    val newSize = minOf(size, remaining())
    return ByteBuffer.allocate(newSize)
        .also {
            this.putTo(it, newSize)
            it.flip()
        }
}

/**
 * ByteArray를 읽어 새로운 ByteBuffer를 빌드합니다.
 */
fun ByteArray.toByteBuffer(): ByteBuffer = ByteBuffer.wrap(this)

/**
 * ByteArray를 읽어 새로운 direct ByteBuffer를 빌드합니다.
 */
fun ByteArray.toByteBufferDirect(): ByteBuffer {
    return ByteBuffer.allocateDirect(this.size)
        .also {
            it.put(this@toByteBufferDirect)
            it.flip()
        }
}
