package io.bluetape4k.io.compressor

import io.bluetape4k.io.DEFAULT_BLOCK_SIZE
import io.bluetape4k.io.toByteArray
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * BZip2 Compressor
 */
class BZip2Compressor(
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE,
): AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        return ByteArrayOutputStream(bufferSize).use { bos ->
            BZip2CompressorOutputStream(bos).use { bzip2 ->
                bzip2.write(plain)
                bzip2.flush()
            }
            bos.toByteArray()
        }
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        return ByteArrayInputStream(compressed).buffered(bufferSize).use { bis ->
            BZip2CompressorInputStream(bis).use { bzip2 ->
                bzip2.toByteArray(DEFAULT_BLOCK_SIZE)
            }
        }
    }
}
