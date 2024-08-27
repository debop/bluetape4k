package io.bluetape4k.io.compressor

import okio.Buffer
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream

class LZMACompressor: AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        val output = Buffer()
        LZMACompressorOutputStream(output.outputStream()).use { lzma ->
            lzma.write(plain)
            lzma.flush()
        }
        return output.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val input = Buffer().write(compressed)
        LZMACompressorInputStream(input.inputStream()).use { lzma ->
            return Buffer().readFrom(lzma).readByteArray()
        }
    }
}
