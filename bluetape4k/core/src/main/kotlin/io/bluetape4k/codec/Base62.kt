package io.bluetape4k.codec

import io.bluetape4k.codec.Base62.DEFAULT_BIT_LIMIT
import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.core.requireZeroOrPositiveNumber
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toBigInt
import io.bluetape4k.support.toUuid
import java.math.BigInteger
import java.util.*


/**
 * Base62 Encoder
 *
 * 참고: [opencoinage Base62.java](https://github.com/opencoinage/opencoinage/blob/master/src/java/org/opencoinage/util/Base62.java)
 */
object Base62: KLogging() {

    private const val DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private val BASE = BigInteger.valueOf(62)
    private val PATTERN = "[$DIGITS]*".toRegex()
    const val DEFAULT_BIT_LIMIT = 128

    fun encode(number: BigInteger): String {
        number.requireZeroOrPositiveNumber("number")
        val result = StringBuilder()

        var current = number
        while (current > BigInteger.ZERO) {
            val (quantient, remainder) = current.divideAndRemainder(BASE)
            current = quantient
            val digit = remainder.intValueExact()
            result.insert(0, DIGITS[digit])
        }
        return if (result.isEmpty()) DIGITS.substring(0, 1) else result.toString()
    }

    @JvmOverloads
    fun decode(text: String, bitLimit: Int = DEFAULT_BIT_LIMIT): BigInteger {
        text.requireNotBlank("text")

        if (!PATTERN.matches(text)) {
            throw IllegalArgumentException("Text `$text` contains illegal characters, only [$DIGITS] are allowed.")
        }

        var sum = BigInteger.ZERO
        text.forEachIndexed { index, _ ->
            sum += DIGITS.indexOf(text[text.length - index - 1]).toBigInteger() * BASE.pow(index)
            if (bitLimit > 0 && sum.bitLength() > bitLimit) {
                throw IllegalArgumentException("Text [$text] contains more than 128bit information")
            }
        }
        return sum
    }
}

/**
 * Base62 알고리즘을 이용하여 UUID 값을 문자열로 인코딩합니다.
 * @return Base62로 인코딩된 문자열
 */
fun <T: Number> T.encodeBase62(): String = Base62.encode(this.toBigInt())

/**
 * Base62 알고리즘으로 인코딩된 문자열을 디코딩하여 [BigInteger] 값을 반환합니다.
 */
fun String.decodeBase62(bitLimit: Int = DEFAULT_BIT_LIMIT): BigInteger = Base62.decode(this, bitLimit)

/**
 * Base62 알고리즘을 이용하여 UUID 값을 문자열로 인코딩합니다.
 * @return Base62로 인코딩된 문자열
 */
fun UUID.encodeBase62(): String = Base62.encode(this.toBigInt())

/**
 * Base62 알고리즘으로 인코딩된 문자열을 디코딩하여 UUID 값을 반환합니다.
 */
fun String.decodeBase62AsUuid(bitLimit: Int = DEFAULT_BIT_LIMIT): UUID = Base62.decode(this, bitLimit).toUuid()
