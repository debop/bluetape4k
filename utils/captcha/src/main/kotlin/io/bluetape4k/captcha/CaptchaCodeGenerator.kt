package io.bluetape4k.captcha

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.requirePositiveNumber
import java.security.SecureRandom
import java.util.*

/**
 * Captcha에 쓰일 임의의 문자열을 생성합니다.
 *
 * @property length
 * @property random
 * @property symbols
 * @constructor Create empty Random string
 */
class CaptchaCodeGenerator private constructor(
    val random: Random,
    val symbols: String,
) {
    companion object: KLogging() {

        const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const val LOWER = "abcdefghijklmnopqrstuvwxyz"
        const val DIGITS = "0123456789"

        const val UPPER_DIGITS = UPPER + DIGITS
        const val ALPHA_DIGITS = UPPER + LOWER + DIGITS

        @JvmStatic
        operator fun invoke(
            random: Random = SecureRandom(),
            symbols: String = ALPHA_DIGITS,
        ): CaptchaCodeGenerator {
            if (symbols.length < 2) throw IllegalArgumentException()

            return CaptchaCodeGenerator(random, symbols)
        }

        @JvmStatic
        operator fun invoke(digitOnly: Boolean): CaptchaCodeGenerator {
            val symbols = if (digitOnly) {
                DIGITS
            } else {
                ALPHA_DIGITS
            }
            return CaptchaCodeGenerator(symbols = symbols)
        }
    }

    /**
     * Generate a random string.
     *
     * @param length 생성할 문자열 길이, 0보다 커야 합니다.
     */
    fun next(length: Int): String {
        length.requirePositiveNumber("length")

        val buf = CharArray(length) {
            symbols[random.nextInt(symbols.length)]
        }
        return String(buf)
    }
}
