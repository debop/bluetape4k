package io.bluetape4k.io.compressor

import com.github.luben.zstd.Zstd
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.coerce
import okio.Buffer
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream

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
        val output = Buffer().buffer()
        ZstdCompressorOutputStream(output.outputStream(), level).use { zstd ->
            zstd.write(plain)
            zstd.flush()
        }
        return output.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val input = Buffer().write(compressed).buffer()
        ZstdCompressorInputStream(input.inputStream()).use { zstd ->
            return Buffer().readFrom(zstd).readByteArray()
        }
    }
}
