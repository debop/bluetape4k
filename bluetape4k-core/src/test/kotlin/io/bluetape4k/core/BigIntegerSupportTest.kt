package io.bluetape4k.core

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigInteger

class BigIntegerSupportTest {

    companion object : KLogging()

    @Test
    fun `compare BigInteger`() {
        Assertions.assertTrue { BigInteger.ZERO < BigInteger.ONE }
        Assertions.assertFalse { BigInteger.ZERO > BigInteger.ONE }
    }

    @Test
    fun `basic operators`() {
        val b = 20.toBigInt()
        val a = 10.toBigInt()

        a + a shouldBeEqualTo b
        b - a shouldBeEqualTo a
        a * 2 shouldBeEqualTo b
        b / 2 shouldBeEqualTo a
    }

    @Test
    fun `collection operator of BigInteger`() {
        val numbers = List(100) { it.toBigInt() }
        numbers.sum() shouldBeEqualTo (0 until 100).sum().toBigInt()
    }
}
