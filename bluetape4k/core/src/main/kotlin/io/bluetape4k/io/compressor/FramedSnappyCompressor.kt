package io.bluetape4k.io.compressor

import okio.Buffer
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream

class FramedSnappyCompressor: AbstractCompressor() {


    override fun doCompress(plain: ByteArray): ByteArray {
        val buffer = Buffer()
        FramedSnappyCompressorOutputStream(buffer.outputStream()).use { snappy ->
            snappy.write(plain)
            snappy.flush()
        }
        return buffer.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val buffer = Buffer().write(compressed)
        FramedSnappyCompressorInputStream(buffer.inputStream()).use { snappy ->
            return Buffer().readFrom(snappy).readByteArray()
        }
    }
}
