package io.bluetape4k.io.compressor

import com.github.luben.zstd.Zstd
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.emptyByteArray
import io.bluetape4k.support.toByteArray
import io.bluetape4k.support.toInt


/**
 * Zstd 알고리즘을 활용한 압축기
 *
 * 참고: [zstd-jni](https://github.com/luben/zstd-jni)
 */
class ZstdCompressor private constructor(val level: Int): AbstractCompressor() {

    companion object: KLogging() {
        private const val MAGIC_NUMBER_SIZE: Int = Integer.BYTES
        const val DEFAULT_LEVEL: Int = 3

        operator fun invoke(level: Int = DEFAULT_LEVEL): ZstdCompressor {
            val cLevel = maxOf(Zstd.minCompressionLevel(), minOf(level, Zstd.maxCompressionLevel()))
            return ZstdCompressor(cLevel)
        }
    }

    override fun doCompress(plain: ByteArray): ByteArray {
        val sourceSize = plain.size
        val maxOutputSize = Zstd.compressBound(plain.size.toLong()).toInt()

        val compressedArray = ByteArray(MAGIC_NUMBER_SIZE + maxOutputSize)
        val compressedSize = Zstd.compressByteArray(
            compressedArray,
            MAGIC_NUMBER_SIZE,
            maxOutputSize - MAGIC_NUMBER_SIZE,
            plain,
            0,
            sourceSize,
            level
        )

        sourceSize.toByteArray().copyInto(compressedArray, 0, 0)
        return compressedArray.copyOfRange(0, compressedSize.toInt() + MAGIC_NUMBER_SIZE)
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val originSize = compressed.toInt()
        if (originSize <= 0) {
            return emptyByteArray
        }

        val originArray = ByteArray(originSize)
        Zstd.decompressByteArray(
            originArray,
            0,
            originSize,
            compressed,
            MAGIC_NUMBER_SIZE,
            compressed.size - MAGIC_NUMBER_SIZE
        )
        return originArray
    }
}
