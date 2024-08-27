package io.bluetape4k.io.compressor

import okio.Buffer
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream

class FramedSnappyCompressor: AbstractCompressor() {


    override fun doCompress(plain: ByteArray): ByteArray {
        val output = Buffer()
        FramedSnappyCompressorOutputStream(output.outputStream()).use { snappy ->
            snappy.write(plain)
            snappy.flush()
        }
        return output.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val input = Buffer().write(compressed)
        FramedSnappyCompressorInputStream(input.inputStream()).use { snappy ->
            return Buffer().readFrom(snappy).readByteArray()
        }
    }
}
