package io.bluetape4k.support

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BigDecimalSupportTest {

    companion object: KLogging()

    @Test
    fun `compare BigDecimal and Number`() {
        Assertions.assertTrue { BigDecimal.ONE > 0L }
        Assertions.assertTrue { BigDecimal.ONE > 0.5 }

        Assertions.assertFalse { BigDecimal.ZERO > 0L }
        Assertions.assertFalse { BigDecimal.ZERO > 0.5 }
    }

    @Test
    fun `basic operators`() {
        val b = 20.toBigDecimal()
        val a = 10.toBigDecimal()

        a + a shouldBeEqualTo b
        b - a shouldBeEqualTo a
        a * 2 shouldBeEqualTo b
        b / 2 shouldBeEqualTo a
    }

    @Test
    fun `collection operator of BigDecimal`() {
        val numbers = List(100) { it.toBigDecimal() }
        numbers.sum() shouldBeEqualTo (0 until 100).sum().toBigDecimal()
    }
}
