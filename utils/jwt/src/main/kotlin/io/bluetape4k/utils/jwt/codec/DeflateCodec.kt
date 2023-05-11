package io.bluetape4k.utils.jwt.codec

import io.bluetape4k.io.compressor.Compressors
import io.bluetape4k.logging.KLogging
import io.jsonwebtoken.impl.compression.AbstractCompressionCodec

class DeflateCodec: AbstractCompressionCodec() {

    companion object: KLogging() {
        const val ALGORITHM = "DEFLATE"
    }

    private val deflate by lazy { Compressors.Deflate }

    override fun getAlgorithmName(): String {
        return ALGORITHM
    }

    override fun doCompress(payload: ByteArray?): ByteArray {
        return deflate.compress(payload)
    }

    override fun doDecompress(compressed: ByteArray?): ByteArray {
        return deflate.decompress(compressed)
    }

}
