package io.bluetape4k.io.compressor

import io.bluetape4k.core.support.emptyByteArray
import io.bluetape4k.core.support.toByteArray
import io.bluetape4k.core.support.toInt
import io.bluetape4k.logging.KLogging
import net.jpountz.lz4.LZ4Factory

/**
 * LZ4 Compressor
 */
class LZ4Compressor : AbstractCompressor() {

    companion object : KLogging() {
        private const val MAGIC_NUMBER_SIZE: Int = Integer.BYTES
        private val factory = LZ4Factory.fastestInstance()
        private val compressor = factory.fastCompressor()
        private val decompressor = factory.fastDecompressor()
    }

    override fun doCompress(plain: ByteArray): ByteArray {
        val sourceSize = plain.size
        val maxOutputSize = compressor.maxCompressedLength(sourceSize)

        val compressedArray = ByteArray(MAGIC_NUMBER_SIZE + maxOutputSize)
        val compressedSize = compressor.compress(
            plain,
            0,
            sourceSize,
            compressedArray,
            MAGIC_NUMBER_SIZE,
            maxOutputSize - MAGIC_NUMBER_SIZE
        )

        sourceSize.toByteArray().copyInto(compressedArray, 0, 0)
        return compressedArray.copyOfRange(0, compressedSize + MAGIC_NUMBER_SIZE)
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val originSize = compressed.toInt()
        if (originSize <= 0) {
            return emptyByteArray
        }

        val originArray = ByteArray(originSize)
        decompressor.decompress(compressed, MAGIC_NUMBER_SIZE, originArray, 0, originArray.size)
        return originArray
    }
}