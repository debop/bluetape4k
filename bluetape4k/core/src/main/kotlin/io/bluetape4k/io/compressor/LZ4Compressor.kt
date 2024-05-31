package io.bluetape4k.io.compressor

import io.bluetape4k.io.getBytes
import io.bluetape4k.io.toByteBufferDirect
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.emptyByteArray
import net.jpountz.lz4.LZ4Compressor
import net.jpountz.lz4.LZ4Factory
import net.jpountz.lz4.LZ4FastDecompressor
import java.nio.ByteBuffer

/**
 * LZ4 알고리즘을 사용한 Compressor
 *
 * @see [LZ4Factory]
 * @see [LZ4Factory.fastestInstance]
 * @see [LZ4Factory.fastCompressor]
 * @see [LZ4Factory.fastDecompressor]
 * @see [LZ4Compressor]
 * @see [LZ4FastDecompressor]
 */
class LZ4Compressor: AbstractCompressor() {

    companion object: KLogging() {
        private const val MAGIC_NUMBER_SIZE: Int = Integer.BYTES
        private val factory: LZ4Factory by lazy { LZ4Factory.fastestInstance() }
        private val compressor: LZ4Compressor by lazy { factory.fastCompressor() }
        private val decompressor: LZ4FastDecompressor by lazy { factory.fastDecompressor() }
    }

    override fun doCompress(plain: ByteArray): ByteArray {
        val sourceSize = plain.size
        val maxOutputSize = compressor.maxCompressedLength(sourceSize)

        val sourceBuffer = plain.toByteBufferDirect()
        val outputBuffer = ByteBuffer.allocateDirect(MAGIC_NUMBER_SIZE + maxOutputSize)
        outputBuffer.putInt(0, sourceSize)

        val outputSize = compressor.compress(
            sourceBuffer,
            0,
            sourceSize,
            outputBuffer,
            MAGIC_NUMBER_SIZE,
            maxOutputSize
        )

        return ByteArray(MAGIC_NUMBER_SIZE + outputSize).apply {
            outputBuffer.get(this, 0, MAGIC_NUMBER_SIZE + outputSize)
        }
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        if (compressed.isEmpty()) {
            return emptyByteArray
        }

        val sourceBuffer = compressed.toByteBufferDirect()
        val originSize = sourceBuffer.getInt(0)
        if (originSize <= 0) {
            return emptyByteArray
        }

        val outputBuffer = ByteBuffer.allocateDirect(originSize)
        decompressor.decompress(
            sourceBuffer,
            MAGIC_NUMBER_SIZE,
            outputBuffer,
            0,
            originSize
        )
        return outputBuffer.getBytes()
    }
}
