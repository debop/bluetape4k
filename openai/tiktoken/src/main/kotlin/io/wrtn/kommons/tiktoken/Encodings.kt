package io.bluetape4k.tiktoken

import io.bluetape4k.logging.KLogging
import io.bluetape4k.tiktoken.api.EncodingRegistry

object Encodings: KLogging() {

    /**
     * Creates a new [EncodingRegistry] with the default encodings found in the [EncodingType] enum.
     *
     * @return the new [EncodingRegistry]
     */
    fun newDefaultEncodingRegistry(): EncodingRegistry {
        return DefaultEncodingRegistry().apply {
            initializeDefaultEncodings()
        }
    }

    /**
     * Creates a new {@link EncodingRegistry} without any {@link EncodingType} registered. Encodings will be
     * loaded on-demand when they are first requested. For example, if you call
     * {@link EncodingRegistry#getEncoding(EncodingType)} with {@link EncodingType#CL100K_BASE} for the first time,
     * it will be loaded from the classpath. Subsequent calls with {@link EncodingType#CL100K_BASE} will re-use the
     * already loaded encoded.
     *
     * @return the new [EncodingRegistry]
     */
    fun newLazyEncodingRegistry(): EncodingRegistry {
        return LazyEncodingRegistry()
    }
}
