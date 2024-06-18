package io.bluetape4k.io.compressor

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.coerce
import okio.Buffer
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream


/**
 * XZ 알고리즘을 사용하는 Compressor
 *
 * ```
 * val xz = XZCompressor()
 * val compresseod = xz.compress(bytes)
 * val decompressed = xz.decompress(compressed)
 * ```
 * @property preset
 *      The presets 0-3 are fast presets with medium compression.
 *      The presets 4-6 are fairly slow presets with high compression.
 *      The default preset is 6.
 *  @property bufferSize The buffer size for compressing and decompressing.
 *
 * @see XZCompressorOutputStream
 * @see XZCompressorInputStream
 */
class XZCompressor private constructor(
    private val preset: Int,
    private val bufferSize: Int,
): AbstractCompressor() {

    companion object: KLogging() {
        private const val DEFAULT_PRESET: Int = 6

        @JvmOverloads
        operator fun invoke(
            preset: Int = DEFAULT_PRESET,
            bufferSize: Int = DEFAULT_BUFFER_SIZE,
        ): XZCompressor {
            val ps = preset.coerce(0, 6)
            return XZCompressor(ps, bufferSize)
        }
    }

    override fun doCompress(plain: ByteArray): ByteArray {
        val output = Buffer().buffer()
        XZCompressorOutputStream(output.outputStream(), preset).use { xz ->
            xz.write(plain)
            xz.flush()
        }
        return output.readByteArray()
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        val input = Buffer().write(compressed).buffer()
        XZCompressorInputStream(input.inputStream()).use { xz ->
            return Buffer().readFrom(xz).readByteArray()
        }
    }
}
