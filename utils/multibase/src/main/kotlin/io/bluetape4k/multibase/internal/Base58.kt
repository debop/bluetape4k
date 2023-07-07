package io.bluetape4k.multibase.internal

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.isNullOrEmpty
import java.math.BigInteger

object Base58: KLogging() {

    val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()
    private val ENCODED_ZERO = ALPHABET[0]
    private val INDEXES = IntArray(128) { -1 }.apply {
        ALPHABET.indices.forEach {
            this[ALPHABET[it].code] = it
        }
    }

    /**
     * Encodes the given bytes as a base58 string (no checksum is appended).
     *
     * @param source the bytes to encode
     * @return the base58-encoded string
     */
    fun encode(source: ByteArray): String {
        if (source.isNullOrEmpty()) {
            return ""
        }

        // Count leading zeros.
        var zeros = 0
        while (zeros < source.size && source[zeros].toInt() == 0) {
            zeros++
        }

        // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
        val input = source.copyOf(source.size)
        val encoded = CharArray(input.size * 2)
        var outputStart = encoded.size
        var inputStart = zeros
        while (inputStart < input.size) {
            encoded[--outputStart] = ALPHABET[divmod(input, inputStart, 256, 58).toInt()]
            if (input[inputStart].toInt() == 0) {
                ++inputStart
            }
        }

        // Preserve exactly as many leading encoded zeros in output as there were leading zeros in input.
        while (outputStart < encoded.size && encoded[outputStart] == ENCODED_ZERO) {
            ++outputStart
        }
        while (--zeros >= 0) {
            encoded[--outputStart] = ENCODED_ZERO
        }
        // Return encoded string (including encoded leading zeros).
        return String(encoded, outputStart, encoded.size - outputStart)
    }

    /**
     * Decodes the given base58 string into the original data bytes.
     *
     * @param input the base58-encoded string to decode
     * @return the decoded data bytes
     */
    fun decode(source: String): ByteArray {
        if (source.isBlank()) {
            return ByteArray(0)
        }

        // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
        val input58 = ByteArray(source.length) {
            val c = source[it]
            val digit = if (c.code < 128) INDEXES[c.code] else -1
            if (digit < 0) {
                throw IllegalArgumentException("Invalid character in Base58: ${c.code.toString(16)}")
            }
            digit.toByte()
        }

        // Count leading zeros.
        var zeros = 0
        while (zeros < input58.size && input58[zeros].toInt() == 0) {
            ++zeros
        }

        // Convert base-58 digits to base-256 digits.
        val decoded = ByteArray(source.length)
        var outputStart = decoded.size
        var inputStart = zeros
        while (inputStart < input58.size) {
            decoded[--outputStart] = divmod(input58, inputStart, 58, 256)
            if (input58[inputStart].toInt() == 0) {
                ++inputStart     // optimization - skip leading zeros
            }
        }

        // Ignore extra leading zeroes that were added during the calculation.
        while (outputStart < decoded.size && decoded[outputStart].toInt() == 0) {
            ++outputStart
        }

        return decoded.copyOfRange(outputStart - zeros, decoded.size)
    }

    fun decodeToBigInteger(input: String): BigInteger {
        return BigInteger(1, decode(input))
    }

    /**
     * Divides a number, represented as an array of bytes each containing a single digit
     * in the specified base, by the given divisor. The given number is modified in-place
     * to contain the quotient, and the return value is the remainder.
     *
     * @param number the number to divide
     * @param firstDigit the index within the array of the first non-zero digit
     *        (this is used for optimization by skipping the leading zeros)
     * @param base the base in which the number's digits are represented (up to 256)
     * @param divisor the number to divide by (up to 256)
     * @return the remainder of the division operation
     */
    private fun divmod(number: ByteArray, firstDigit: Int, base: Int, divisor: Int): Byte {
        // this is just long division which accounts for the base of the input digits
        var remainder = 0
        for (i in firstDigit until number.size) {
            val digit = number[i].toInt() and 0xFF
            val temp = remainder * base + digit
            number[i] = (temp / divisor).toByte()
            remainder = temp % divisor
        }
        return remainder.toByte()
    }
}
