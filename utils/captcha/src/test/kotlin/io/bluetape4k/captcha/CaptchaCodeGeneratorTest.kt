package io.bluetape4k.captcha

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.RepeatedTest

class CaptchaCodeGeneratorTest: AbstractCaptchaTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `특정 길이의 랜덤 Digit 문자열을 생성합니다`() {
        val captchaCodeGenerator = CaptchaCodeGenerator(digitOnly = true)

        val str = captchaCodeGenerator.next(6)
        log.debug { "random string=$str" }
        str.length shouldBeEqualTo 6

        // digit only
        str.all { it.isDigit() }.shouldBeTrue()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `특정 길이의 랜덤 Alpha Numeric 문자열을 생성합니다`() {
        val captchaCodeGenerator = CaptchaCodeGenerator(symbols = CaptchaCodeGenerator.ALPHA_DIGITS)

        val str = captchaCodeGenerator.next(6)
        log.debug { "random string=$str" }
        str.length shouldBeEqualTo 6

        // digit only
        str.all { it.isLetterOrDigit() }.shouldBeTrue()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `특정 길이의 랜덤 Upper Numeric 문자열을 생성합니다`() {
        val captchaCodeGenerator = CaptchaCodeGenerator(symbols = CaptchaCodeGenerator.UPPER_DIGITS)

        val str = captchaCodeGenerator.next(6)
        log.debug { "random string=$str" }
        str.length shouldBeEqualTo 6

        // digit only
        str.all { it.isDigit() || it.isUpperCase() }.shouldBeTrue()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `특정 길이의 랜덤 Upper 문자열을 생성합니다`() {
        val captchaCodeGenerator = CaptchaCodeGenerator(symbols = CaptchaCodeGenerator.UPPER)

        val str = captchaCodeGenerator.next(6)
        log.debug { "random string=$str" }
        str.length shouldBeEqualTo 6

        // digit only
        str.all { it.isUpperCase() }.shouldBeTrue()
    }
}
