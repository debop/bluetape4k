package io.bluetape4k.codec

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.logging.KLogging

class AsciiRadixCoder private constructor(
    private val alphabet: String,
) {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(alphabet: String): AsciiRadixCoder {
            alphabet.requireNotBlank("alphabet")
            return AsciiRadixCoder(alphabet)
        }

        @JvmStatic
        private fun checkAscii(c: Char): Int {
            require(c.code < 1 shl 7) { "char '${c.code}' is not ascii" }
            return c.code
        }
    }

    private val chars: ByteArray
    private val digits: ByteArray
    private val byteCoder: RadixCoder<ByteArray>

    init {
        chars = ByteArray(alphabet.length)
        digits = ByteArray(1 shl 7) { -1 }

        chars.indices.forEach { i ->
            val c = checkAscii(alphabet[i])
            chars[i] = c.toByte()
            if (digits[c].toInt() != -1) throw IllegalArgumentException("char[$c] is repeated in alphabet[$alphabet]")
            digits[c] = i.toByte()
        }
        byteCoder = RadixCoder.u8(chars.size)
    }

    fun base(): Int = chars.size

    fun alphabet(): String = String(chars, Charsets.US_ASCII)

    fun encode(bytes: ByteArray): String {
        val bs = byteCoder.encode(bytes)
        bs.indices.forEach { i ->
            bs[i] = chars[bs[i].toInt()]
        }
        return String(bs, Charsets.US_ASCII)
    }

    fun decode(s: String): ByteArray {
        val bs = ByteArray(s.length)
        bs.indices.forEach { i ->
            val c = checkAscii(s[i])
            val digit = digits[c]
            if (digit.toInt() == -1) throw IllegalArgumentException("char[$c] is not present in alphabet[$alphabet]")
            bs[i] = digit
        }
        return byteCoder.decode(bs)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other != null && other is AsciiRadixCoder)
            return chars.contentEquals(other.chars)

        return false
    }

    override fun hashCode(): Int = chars.contentHashCode()

    override fun toString(): String = "AsciiRadixCoder($alphabet)"
}
