package io.bluetape4k.support

import io.bluetape4k.logging.KLogging
import java.math.BigDecimal
import java.math.BigInteger
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class NumberExtensionsTest {

    companion object : KLogging()

    @Test
    fun `coerceIn operator`() {

        32.coerce(38, 42) shouldBeEqualTo 38
        32.coerceIn(38..42) shouldBeEqualTo 38

        44.coerce(38, 42) shouldBeEqualTo 42
        44.coerceIn(38..42) shouldBeEqualTo 42

        40.coerce(38, 42) shouldBeEqualTo 40
        40.coerceIn(38..42) shouldBeEqualTo 40
    }

    @Test
    fun `string is hex number`() {
        "0x74".isHexNumber().shouldBeTrue()
        "0XHH".isHexNumber().shouldBeTrue()
        "#3A".isHexNumber().shouldBeTrue()
        "-0xAD".isHexNumber().shouldBeTrue()

        "X0FF".isHexNumber().shouldBeFalse()
    }

    @Test
    fun `decode string to BigInteger`() {
        "".decodeBigInt() shouldBeEqualTo BigInteger.ZERO
        "-1".decodeBigInt() shouldBeEqualTo BigInteger.ONE.negate()
        "#42".decodeBigInt() shouldBeEqualTo BigInteger.valueOf(0x42L)
        "0x42".decodeBigInt() shouldBeEqualTo BigInteger.valueOf(0x42L)
    }

    @Test
    fun `decode string to BigDecimal`() {
        "".decodeBigDecimal() shouldBeEqualTo BigDecimal.ZERO
        "-1".decodeBigDecimal() shouldBeEqualTo BigDecimal.ONE.negate()

        assertFailsWith<NumberFormatException> {
            "#42".decodeBigDecimal() shouldBeEqualTo BigDecimal.valueOf(0x42L)
        }
        assertFailsWith<NumberFormatException> {
            "0x42".decodeBigDecimal() shouldBeEqualTo BigDecimal.valueOf(0x42L)
        }
    }

    @Test
    fun `parse string to number`() {
        "0x42".parseNumber<Int>() shouldBeEqualTo 0x42
        "-0x42".parseNumber<Int>() shouldBeEqualTo -0x42

        " 42 ".parseNumber<Int>() shouldBeEqualTo 42
        " 42 ".parseNumber<Long>() shouldBeEqualTo 42L

        "42".parseNumber<BigInteger>() shouldBeEqualTo 42.toBigInt()

        "42.4".parseNumber<Float>() shouldBeEqualTo 42.4F
        "42.4".parseNumber<Double>() shouldBeEqualTo 42.4

        "42.4".parseNumber<BigDecimal>() shouldBeEqualTo 42.4.toBigDecimal()
    }
}
