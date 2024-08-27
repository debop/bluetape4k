package io.bluetape4k.io.compressor

import okio.Buffer
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream

class FramedLZ4Compressor: AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        val output = Buffer().buffer()
        FramedLZ4CompressorOutputStream(output.outputStream()).use { lz4 ->
            lz4.write(plain)
            lz4.flush()
        }
        return output.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val input = Buffer().write(compressed).buffer()
        FramedLZ4CompressorInputStream(input.inputStream()).use { lz4 ->
            return Buffer().readFrom(lz4).readByteArray()
        }
    }
}
