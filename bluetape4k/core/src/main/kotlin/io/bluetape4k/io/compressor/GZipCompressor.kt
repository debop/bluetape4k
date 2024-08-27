package io.bluetape4k.io.compressor

import okio.Buffer
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * GZip 알고리즘을 이용한 압축/복원
 *
 * @see [GZIPOutputStream]
 * @see [GZIPInputStream]
 */
class GZipCompressor @JvmOverloads constructor(
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE,
): AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        val output = Buffer()
        GZIPOutputStream(output.outputStream()).use { gzip ->
            gzip.write(plain)
            gzip.finish()
        }
        return output.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val input = Buffer().write(compressed)
        GZIPInputStream(input.inputStream()).use { gzip ->
            return Buffer().readFrom(gzip).readByteArray()
        }
    }
}
