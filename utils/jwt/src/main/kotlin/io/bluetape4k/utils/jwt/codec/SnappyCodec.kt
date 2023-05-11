package io.bluetape4k.utils.jwt.codec

import io.bluetape4k.io.compressor.Compressors
import io.bluetape4k.io.compressor.SnappyCompressor
import io.bluetape4k.logging.KLogging
import io.jsonwebtoken.impl.compression.AbstractCompressionCodec

class SnappyCodec: AbstractCompressionCodec() {

    companion object: KLogging() {
        const val ALGORITHM = "SNAPPY"
    }

    private val snappy: SnappyCompressor by lazy { Compressors.Snappy }

    override fun getAlgorithmName(): String {
        return ALGORITHM
    }

    override fun doCompress(payload: ByteArray?): ByteArray {
        return snappy.compress(payload)
    }

    override fun doDecompress(compressed: ByteArray?): ByteArray {
        return snappy.decompress(compressed)
    }
}
