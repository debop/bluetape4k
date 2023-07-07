package io.bluetape4k.multibase.internal

import io.bluetape4k.logging.KLogging
import java.math.BigInteger

object Base36: KLogging() {

    fun decode(source: String): ByteArray {
        val withoutLeadingZeros = BigInteger(source, 36).toByteArray()
        val zeroPrefixLength = zeroPrefixLength(source)
        val res = ByteArray(zeroPrefixLength + withoutLeadingZeros.size)
        withoutLeadingZeros.copyInto(res, zeroPrefixLength, 0, withoutLeadingZeros.size)
        return res
    }

    fun encode(source: ByteArray): String {
        val withoutLeadingZeros = BigInteger(1, source).toString(36)
        val zeroPrefixLength = zeroPrefixLength(source)

        return buildString {
            repeat(zeroPrefixLength) {
                append("0")
            }
            append(withoutLeadingZeros)
        }
    }

    private fun zeroPrefixLength(bytes: ByteArray): Int {
        bytes.forEachIndexed { i, b ->
            if (b.toInt() != 0) {
                return i
            }
        }
        return bytes.size
    }

    private fun zeroPrefixLength(data: String): Int {
        data.forEachIndexed { i, c ->
            if (c != '0') {
                return i
            }
        }
        return data.length
    }
}
