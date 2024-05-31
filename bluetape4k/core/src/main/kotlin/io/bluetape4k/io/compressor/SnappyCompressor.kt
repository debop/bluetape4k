package io.bluetape4k.io.compressor

import org.xerial.snappy.Snappy

/**
 * Snappy 알고리즘을 사용한 Compressor
 */
class SnappyCompressor: AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        return Snappy.compress(plain)
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        return Snappy.uncompress(compressed)
    }
}
