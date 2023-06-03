package io.bluetape4k.io.compressor

import io.bluetape4k.logging.KLogging
import io.netty.util.internal.PlatformDependent

//@DisabledIf(value = "isNotSupported", disabledReason = "Brotli is not supported on this platform")
class BrotliCompressorTest: AbstractCompressorTest() {
    companion object: KLogging() {
        @JvmStatic
        fun isNotSupported(): Boolean =
            PlatformDependent.isOsx() && PlatformDependent.normalizedArch() == "aarch_64" // Apple Silicon
    }

    override val compressor: Compressor = BrotliCompressor()
}
