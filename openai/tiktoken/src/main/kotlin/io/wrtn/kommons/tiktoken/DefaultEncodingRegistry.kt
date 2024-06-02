package io.bluetape4k.tiktoken

import io.bluetape4k.tiktoken.api.EncodingType

/**
 * Thread-safe default implementation of {@link EncodingRegistry}.
 * During initialization, it registers the default encodings for the different [EncodingType]s.
 */
class DefaultEncodingRegistry: AbstractEncodingRegistry() {

    /**
     * Initializes the registry with the default encodings.
     *
     * @throws IllegalStateException if an unknown encoding type is encountered
     */
    fun initializeDefaultEncodings() {
        EncodingType.entries.forEach {
            addEncoding(it)
        }
    }
}
