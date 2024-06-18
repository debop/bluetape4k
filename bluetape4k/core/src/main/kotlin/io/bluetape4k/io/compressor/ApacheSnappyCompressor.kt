package io.bluetape4k.io.compressor

import okio.Buffer
import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream
import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream

class ApacheSnappyCompressor: AbstractCompressor() {


    override fun doCompress(plain: ByteArray): ByteArray {
        val buffer = Buffer()
        SnappyCompressorOutputStream(buffer.outputStream(), plain.size.toLong()).use { snappy ->
            snappy.write(plain)
            snappy.flush()
        }
        return buffer.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val buffer = Buffer().write(compressed)
        SnappyCompressorInputStream(buffer.inputStream()).use { snappy ->
            return Buffer().readFrom(snappy).readByteArray()
        }
    }
}
