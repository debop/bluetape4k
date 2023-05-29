package io.bluetape4k.io.compressor

import io.bluetape4k.io.DEFAULT_BLOCK_SIZE
import io.bluetape4k.io.toByteArray
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * GZip 알고리즘을 이용한 압축/복원
 */
class GZipCompressor @JvmOverloads constructor(
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE,
): AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        return ByteArrayOutputStream(bufferSize).use { bos ->
            GZIPOutputStream(bos, bufferSize, false).use { gzip ->
                gzip.write(plain)
                gzip.finish()
            }
            bos.toByteArray()
        }
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        return ByteArrayInputStream(compressed).buffered(bufferSize).use { bis ->
            GZIPInputStream(bis, bufferSize).use { gzip ->
                gzip.toByteArray(DEFAULT_BLOCK_SIZE)
            }
        }
    }
}
