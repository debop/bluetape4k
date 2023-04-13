package io.bluetape4k.io.compressor

import com.aayushatharva.brotli4j.Brotli4jLoader
import com.aayushatharva.brotli4j.decoder.BrotliInputStream
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream
import com.aayushatharva.brotli4j.encoder.Encoder
import io.bluetape4k.core.support.classIsPresent
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Brotli compressor
 *
 * @property options
 */
class BrotliCompressor private constructor(
    private val options: BrotliOptions,
) : AbstractCompressor() {

    companion object : KLogging() {
        private var brotliIsPresent: Boolean = false
        private var cause: Throwable? = null

        init {
            brotliIsPresent = classIsPresent("com.aayushatharva.brotli4j.Brotli4jLoader")
            if (brotliIsPresent) {
                cause = Brotli4jLoader.getUnavailabilityCause()
                if (cause != null) {
                    log.debug(cause) { "Fail to load brotli4j; Brotli support will be unavailable." }
                }
            }
        }

        val isAvailable: Boolean by lazy { brotliIsPresent && Brotli4jLoader.isAvailable() }
        fun ensureAvailability() {
            if (brotliIsPresent) {
                Brotli4jLoader.ensureAvailability()
            } else {
                throw ClassNotFoundException("Class not found. class=com.aayushatharva.brotli4j.Brotli4jLoader")
            }
        }

        @JvmField
        val DefaultBrotliOptions: BrotliOptions = BrotliOptions()

        operator fun invoke(options: BrotliOptions = DefaultBrotliOptions): BrotliCompressor {
            return BrotliCompressor(options)
        }
    }

    override fun doCompress(plain: ByteArray): ByteArray {
        return ByteArrayOutputStream(options.bufferSize).use { bos ->
            val params = Encoder.Parameters().setQuality(options.quality)
            BrotliOutputStream(bos, params).use { brotli ->
                brotli.write(plain)
                brotli.flush()
            }
            bos.toByteArray()
        }
    }

    override fun doDecompress(compressed: ByteArray): ByteArray {
        return ByteArrayInputStream(compressed).buffered(options.bufferSize).use { bis ->
            BrotliInputStream(bis, options.bufferSize).use { brotli ->
                brotli.readAllBytes()
            }
        }
    }

    data class BrotliOptions(
        val bufferSize: Int = DEFAULT_BUFFER_SIZE,
        val quality: Int = 4,
    )
}
