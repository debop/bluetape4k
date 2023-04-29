package io.bluetape4k.io.compressor

import io.bluetape4k.io.toByteArray
import io.bluetape4k.logging.KLogging
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream


/**
 * XZ 알고리즘을 사용하는 Compressor
 *
 * ```
 * val xz = XZCompressor()
 * val compresseod = xz.compress(bytes)
 * ```
 * @property preset
 *      The presets 0-3 are fast presets with medium compression.
 *      The presets 4-6 are fairly slow presets with high compression.
 *      The default preset is 6.
 */
class XZCompressor private constructor(
    private val preset: Int,
    private val bufferSize: Int,
) : AbstractCompressor() {

    companion object : KLogging() {
        private const val DEFAULT_PRESET: Int = 6

        @JvmOverloads
        operator fun invoke(
            preset: Int = DEFAULT_PRESET,
            bufferSize: Int = DEFAULT_BUFFER_SIZE,
        ): XZCompressor {
            val ps = maxOf(0, minOf(preset, 6))
            return XZCompressor(ps, bufferSize)
        }
    }

    override fun doCompress(plain: ByteArray): ByteArray {
        return ByteArrayOutputStream().use { bos ->
            XZCompressorOutputStream(bos, preset).use { xz ->
                xz.write(plain)
                xz.flush()
            }
            bos.toByteArray()
        }
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        return ByteArrayInputStream(compressed).buffered(bufferSize).use { bis ->
            XZCompressorInputStream(bis).use { xz ->
                xz.toByteArray(bufferSize)
            }
        }
    }
}
