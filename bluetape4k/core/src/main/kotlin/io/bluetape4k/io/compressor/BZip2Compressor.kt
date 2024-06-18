package io.bluetape4k.io.compressor

import okio.Buffer
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream

/**
 * BZip2 알고리즘을 사용한 Compressor
 */
class BZip2Compressor(
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE,
): AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        val output = Buffer()
        BZip2CompressorOutputStream(output.outputStream()).use { bzip2 ->
            bzip2.write(plain)
            bzip2.flush()
        }
        return output.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val input = Buffer().write(compressed)
        BZip2CompressorInputStream(input.inputStream()).use { bzip2 ->
            return Buffer().readFrom(bzip2).readByteArray()
        }
    }
}
