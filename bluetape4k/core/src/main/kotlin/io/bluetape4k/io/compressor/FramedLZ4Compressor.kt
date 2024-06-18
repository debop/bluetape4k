package io.bluetape4k.io.compressor

import okio.Buffer
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream

class FramedLZ4Compressor: AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        val buffer = Buffer().buffer()
        FramedLZ4CompressorOutputStream(buffer.outputStream()).use { lz4 ->
            lz4.write(plain)
            lz4.flush()
        }
        return buffer.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val buffer = Buffer().write(compressed).buffer()
        FramedLZ4CompressorInputStream(buffer.inputStream()).use { lz4 ->
            return Buffer().readFrom(lz4).readByteArray()
        }
    }
}
