package io.bluetape4k.io.compressor

import io.bluetape4k.io.toByteArray
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

/**
 * DeflateCompressor
 */
class DeflateCompressor @JvmOverloads constructor(
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE,
): AbstractCompressor() {

    override fun doCompress(plain: ByteArray): ByteArray {
        return ByteArrayOutputStream(bufferSize).use { bos ->
            DeflaterOutputStream(bos).use { deflater ->
                deflater.write(plain)
            }
            bos.toByteArray()
        }
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        return ByteArrayInputStream(compressed).buffered(bufferSize).use { bis ->
            InflaterInputStream(bis).use { inflater ->
                inflater.toByteArray(DEFAULT_BUFFER_SIZE)
            }
        }
    }
}
