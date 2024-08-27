package io.bluetape4k.io.compressor

import okio.Buffer
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

/**
 * Deflate 알고리즘을 사용한 압축기
 *
 * @see [DeflaterOutputStream]
 * @see [InflaterInputStream]
 */
class DeflateCompressor @JvmOverloads constructor(
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE,
): AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        val output = Buffer()
        DeflaterOutputStream(output.outputStream()).use { deflater ->
            deflater.write(plain)
            deflater.finish()
        }
        return output.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val input = Buffer().write(compressed)
        InflaterInputStream(input.inputStream()).use { inflater ->
            return Buffer().readFrom(inflater).readByteArray()
        }
    }
}
