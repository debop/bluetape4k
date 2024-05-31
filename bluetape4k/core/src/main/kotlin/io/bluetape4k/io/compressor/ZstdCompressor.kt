package io.bluetape4k.io.compressor

import com.github.luben.zstd.Zstd
import io.bluetape4k.io.getBytes
import io.bluetape4k.io.toByteBufferDirect
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.coerce
import io.bluetape4k.support.emptyByteArray
import java.nio.ByteBuffer

/**
 * Zstd 알고리즘을 활용한 압축기
 *
 * 참고: [zstd-jni](https://github.com/luben/zstd-jni)
 *
 * @property level 압축 레벨
 */
class ZstdCompressor private constructor(val level: Int): AbstractCompressor() {

    companion object: KLogging() {
        private const val MAGIC_NUMBER_SIZE: Int = Integer.BYTES
        const val DEFAULT_LEVEL: Int = 3

        operator fun invoke(level: Int = DEFAULT_LEVEL): ZstdCompressor {
            val cLevel = level.coerce(Zstd.minCompressionLevel(), Zstd.maxCompressionLevel())
            return ZstdCompressor(cLevel)
        }
    }

    override fun doCompress(plain: ByteArray): ByteArray {
        val sourceSize = plain.size
        val maxOutputSize = Zstd.compressBound(plain.size.toLong()).toInt()

        val sourceBuffer = plain.toByteBufferDirect()
        val outputBuffer = ByteBuffer.allocateDirect(MAGIC_NUMBER_SIZE + maxOutputSize)
        outputBuffer.putInt(0, sourceSize)

        val outputSize = Zstd.compressDirectByteBuffer(
            outputBuffer,
            MAGIC_NUMBER_SIZE,
            maxOutputSize - MAGIC_NUMBER_SIZE,
            sourceBuffer,
            0,
            sourceBuffer.remaining(),
            level,
        ).toInt()

        return ByteArray(MAGIC_NUMBER_SIZE + outputSize).apply {
            outputBuffer.get(this, 0, MAGIC_NUMBER_SIZE + outputSize)
        }
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        if (compressed.isEmpty()) {
            return emptyByteArray
        }
        val sourceBuffer = compressed.toByteBufferDirect()
        val outputSize = sourceBuffer.getInt(0)
        if (outputSize <= 0) {
            return emptyByteArray
        }

        val outputBuffer = ByteBuffer.allocateDirect(outputSize)
        Zstd.decompressDirectByteBuffer(
            outputBuffer,
            0,
            outputSize,
            sourceBuffer,
            MAGIC_NUMBER_SIZE,
            sourceBuffer.remaining() - MAGIC_NUMBER_SIZE,
        )

        return outputBuffer.getBytes()
    }
}
