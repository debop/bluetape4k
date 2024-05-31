package io.bluetape4k.io.compressor

import com.aayushatharva.brotli4j.Brotli4jLoader
import com.aayushatharva.brotli4j.decoder.BrotliInputStream
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream
import com.aayushatharva.brotli4j.encoder.Encoder
import io.bluetape4k.io.ApacheByteArrayOutputStream
import io.bluetape4k.io.compressor.BrotliCompressor.BrotliOptions
import io.bluetape4k.io.compressor.BrotliCompressor.BrotliOptions.Companion.DEFAULT_BUFFER_SIZE
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.classIsPresent
import java.io.ByteArrayInputStream

/**
 * Brotli 알고리즘을 사용한 압축기
 *
 * @property options [BrotliOptions] 인스턴스
 */
class BrotliCompressor private constructor(
    private val options: BrotliOptions,
): AbstractCompressor() {

    companion object: KLogging() {
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

        operator fun invoke(options: BrotliOptions = BrotliOptions.defaults()): BrotliCompressor {
            return BrotliCompressor(options)
        }
    }

    override fun doCompress(plain: ByteArray): ByteArray {
        return ApacheByteArrayOutputStream(options.bufferSize).use { bos ->
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

    /**
     * Brotli 압축을 위한 옵션
     *
     * @property bufferSize 버퍼 크기 (기본: [DEFAULT_BUFFER_SIZE] = 8192)
     * @property quality    품질 (기본: 4)
     * @constructor Create empty Brotli options
     */
    data class BrotliOptions(
        val bufferSize: Int = DEFAULT_BUFFER_SIZE,
        val quality: Int = 4,
    ) {
        companion object {
            const val DEFAULT_BUFFER_SIZE: Int = 8192

            fun defaults(): BrotliOptions = BrotliOptions()
        }
    }
}
