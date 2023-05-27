package io.bluetape4k.io

import io.bluetape4k.core.assertZeroOrPositiveNumber
import io.bluetape4k.logging.KLogging
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * [ByteBuffer]를 저장소로 사용하는  [InputStream] 구현체입니다.
 */
open class ByteBufferInputStream private constructor(
    private val buffer: ByteBuffer,
): InputStream() {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(bufferSize: Int = DEFAULT_BUFFER_SIZE): ByteBufferInputStream {
            return ByteBufferInputStream(ByteBuffer.allocate(bufferSize))
        }

        @JvmStatic
        operator fun invoke(bytes: ByteArray): ByteBufferInputStream {
            return ByteBufferInputStream(bytes.toByteBuffer())
        }

        @JvmStatic
        operator fun invoke(buffer: ByteBuffer): ByteBufferInputStream {
            return ByteBufferInputStream(buffer)
        }
    }

    override fun read(): Int {
        return if (buffer.hasRemaining()) (buffer.get().toInt() and 0xFF) else -1
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        off.assertZeroOrPositiveNumber("off")
        len.assertZeroOrPositiveNumber("len")

        if (len == 0) {
            return 0
        }
        val count = minOf(buffer.remaining(), len)
        if (count == 0) {
            return -1
        }

        buffer.get(b, off, len)
        return count
    }

    override fun available(): Int = buffer.remaining()
}
