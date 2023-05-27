package io.bluetape4k.io.compressor

import org.xerial.snappy.Snappy

/**
 * Snappy Compressor
 */
class SnappyCompressor: AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        return Snappy.compress(plain)
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        return Snappy.uncompress(compressed)
    }
}
