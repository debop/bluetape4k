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
        val buffer = Buffer().buffer()
        BZip2CompressorOutputStream(buffer.outputStream()).use { bzip2 ->
            bzip2.write(plain)
            bzip2.flush()
        }
        return buffer.readByteArray()
//        return ApacheByteArrayOutputStream(bufferSize).use { bos ->
//            BZip2CompressorOutputStream(bos).use { bzip2 ->
//                bzip2.write(plain)
//                bzip2.flush()
//            }
//            bos.toByteArray()
//        }
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val buffer = Buffer().write(compressed).buffer()
        BZip2CompressorInputStream(buffer.inputStream()).use { bzip2 ->
            return Buffer().readFrom(bzip2).readByteArray()
        }

//        return ByteArrayInputStream(compressed).buffered(bufferSize).use { bis ->
//            BZip2CompressorInputStream(bis).use { bzip2 ->
//                bzip2.toByteArray(DEFAULT_BLOCK_SIZE)
//            }
//        }
    }
}
