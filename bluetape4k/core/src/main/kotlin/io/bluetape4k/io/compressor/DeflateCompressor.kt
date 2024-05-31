package io.bluetape4k.io.compressor

import io.bluetape4k.io.ApacheByteArrayOutputStream
import io.bluetape4k.io.DEFAULT_BLOCK_SIZE
import io.bluetape4k.io.toByteArray
import java.io.ByteArrayInputStream
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
        return ApacheByteArrayOutputStream(bufferSize).use { bos ->
            DeflaterOutputStream(bos).use { deflater ->
                deflater.write(plain)
            }
            bos.toByteArray()
        }
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        return ByteArrayInputStream(compressed).buffered(bufferSize).use { bis ->
            InflaterInputStream(bis).use { inflater ->
                inflater.toByteArray(DEFAULT_BLOCK_SIZE)
            }
        }
    }
}
