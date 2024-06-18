package io.bluetape4k.io.compressor

import org.xerial.snappy.Snappy

/**
 * Snappy 알고리즘을 사용한 Compressor
 *
 * Snappy 가 Apache Commons Compress 의 FramedSnappyCompressorOutputStream 보다 훨씬 빠르다. (대략 2배 빠르다)
 *
 * @see [FramedSnappyCompressor]
 */
class SnappyCompressor: AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        return Snappy.compress(plain)
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        return Snappy.uncompress(compressed)
    }
}
