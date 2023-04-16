package io.bluetape4k.codec

import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import java.math.BigInteger
import java.util.UUID
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertFailsWith

@RandomizedTest
class Base62Test {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 10
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `encode decode long value`(@RandomValue(type = BigInteger::class, size = 20) expectes: List<BigInteger>) {
        expectes.forEach { expected ->
            Base62.decode(Base62.encode(expected)) shouldBeEqualTo expected
        }
    }

    @Test
    fun `decoding value prefixed with zeros`() {
        Base62.encode(Base62.decode("00001")) shouldBeEqualTo "1"
        Base62.encode(Base62.decode("01001")) shouldBeEqualTo "1001"
        Base62.encode(Base62.decode("00abcd")) shouldBeEqualTo "abcd"
    }

    @Test
    fun `check 128 bit limits`() {
        assertFailsWith<IllegalArgumentException> {
            Base62.decode("1Vkp6axDWu5pI3q1xQO3oO0")
        }
    }

    @Test
    fun `encode base 62 for Long`() {
        val expectes = List(10) { Random.nextLong(0, 1000000000L) }
        expectes.forEach { expected ->
            expected.encodeBase62().decodeBase62().toLong() shouldBeEqualTo expected
        }
    }

    @Test
    fun `encode base 62 for UUID`() {
        val expectes = List(10) { UUID.randomUUID() }
        expectes.forEach { expected ->
            expected.encodeBase62().decodeBase62AsUuid() shouldBeEqualTo expected
            Url62.decode(Url62.encode(expected)) shouldBeEqualTo expected
        }
    }
}
