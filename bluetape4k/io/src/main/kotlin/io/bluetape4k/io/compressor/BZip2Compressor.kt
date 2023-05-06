package io.bluetape4k.io.compressor

import io.bluetape4k.io.toByteArray
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * BZip2 Compressor
 */
class BZip2Compressor: AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        return ByteArrayOutputStream().use { bos ->
            BZip2CompressorOutputStream(bos).use { bzip2 ->
                bzip2.write(plain)
                bzip2.flush()
            }
            bos.toByteArray()
        }
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        return ByteArrayInputStream(compressed).buffered(DEFAULT_BUFFER_SIZE).use { bis ->
            BZip2CompressorInputStream(bis).use { bzip2 ->
                bzip2.toByteArray(DEFAULT_BUFFER_SIZE)
            }
        }
    }
}
