package io.bluetape4k.io.compressor

import io.bluetape4k.logging.KLogging
import okio.Buffer
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream

/**
 * LZ4 알고리즘을 사용한 Compressor
 *
 * @see [BlockLZ4CompressorInputStream]
 * @see [BlockLZ4CompressorOutputStream]
 */
class LZ4Compressor: AbstractCompressor() {

    companion object: KLogging()

    override fun doCompress(plain: ByteArray): ByteArray {
        val output = Buffer().buffer()
        BlockLZ4CompressorOutputStream(output.outputStream()).use { lz4 ->
            lz4.write(plain)
            lz4.flush()
        }
        return output.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val input = Buffer().write(compressed).buffer()
        BlockLZ4CompressorInputStream(input.inputStream()).use { lz4 ->
            return Buffer().readFrom(lz4).readByteArray()
        }

    }
}
