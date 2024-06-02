package io.bluetape4k.tiktoken

import io.bluetape4k.logging.KLogging
import java.util.concurrent.ConcurrentHashMap

/**
 * A TokenEncoder is used to encode and decode tokens. It is initialized with a map
 * that contains the decoded tokens as keys and the encoded tokens as values. The
 * TokenEncoder can then be used to encode and decode tokens.
 *
 * @param <K> the type of the decoded tokens
 * @param <V> the type of the encoded tokens
 */
internal class TokenEncoder<K, V> {

    companion object: KLogging() {
        @JvmStatic
        operator fun <K, V> invoke(input: Map<K, V>): TokenEncoder<K, V> {
            return invoke(input) { it }
        }

        @JvmStatic
        operator fun <T, K, V> invoke(input: Map<T, V>, keyMapper: (T) -> K): TokenEncoder<K, V> {
            return TokenEncoder<K, V>().apply {
                input.forEach { (t, v) ->
                    val key = keyMapper(t)
                    decodedToEncoded[key] = v
                    encodedToDecoded[v] = key
                }
            }
        }
    }

    private val decodedToEncoded = ConcurrentHashMap<K, V>()
    private val encodedToDecoded = ConcurrentHashMap<V, K>()


    /**
     * Checks if the given decoded token is contained in this encoder.
     *
     * @param decodedToken the decoded token
     * @return true if the decoded token is contained in this encoder, false otherwise
     */
    fun containsDecodedToken(decodedToken: K): Boolean {
        return decodedToEncoded.containsKey(decodedToken)
    }

    /**
     * Encodes the given decoded token.
     *
     * @param decodedToken the decoded token
     * @return the encoded token
     * @throws IllegalArgumentException if the decoded token is not contained in this encoder
     */
    fun encode(decodedToken: K): V {
        return decodedToEncoded[decodedToken]
            ?: throw IllegalArgumentException("Unknown token for encoding: $decodedToken")
    }

    /**
     * Encodes the given decoded token if it is contained in this encoder. Otherwise,
     * an empty optional is returned.
     *
     * @param decodedToken the decoded token
     * @return the encoded token or an empty optional
     */
    fun encodeOrNull(decodedToken: K): V? {
        return decodedToEncoded[decodedToken]
    }

    /**
     * Decodes the given encoded token if it is contained in this encoder. Otherwise,
     * an empty optional is returned.
     *
     * @param encodedToken the encoded token
     * @return the decoded token or an empty optional
     */
    fun decodeOrNull(encodedToken: V): K? {
        return encodedToDecoded[encodedToken]
    }

    /**
     * Returns an unmodifiable set of all decoded tokens contained in this encoder.
     *
     * @return an unmodifiable set of all decoded tokens
     */
    fun getDecodedTokens(): Set<K> {
        return decodedToEncoded.keys
    }
}
