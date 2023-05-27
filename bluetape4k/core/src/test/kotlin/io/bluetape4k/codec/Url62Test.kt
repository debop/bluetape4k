package io.bluetape4k.codec

import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertFailsWith

@RandomizedTest
class Url62Test {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 10
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `encode uuid and decode url62 text`(@RandomValue(type = UUID::class, size = 20) uuids: List<UUID>) {
        uuids.forEach { uuid ->
            val encoded = Url62.encode(uuid)
            log.debug { "uuid=$uuid, encoded=$encoded" }
            Url62.decode(encoded) shouldBeEqualTo uuid
        }
    }

    @Test
    fun `fail when illegal character`() {
        assertFailsWith<IllegalArgumentException> {
            Url62.decode("Foo Bar")
        }
    }

    @Test
    fun `fail when blank string`() {
        assertFailsWith<IllegalArgumentException> {
            Url62.decode("")
        }

        assertFailsWith<IllegalArgumentException> {
            Url62.decode(" \t ")
        }
    }

    @Test
    fun `fail when text contains more than 128 bit information`() {
        assertFailsWith<IllegalArgumentException> {
            Url62.decode("7NLCAyd6sKR7kDHxgAWFPas")
        }
    }
}
