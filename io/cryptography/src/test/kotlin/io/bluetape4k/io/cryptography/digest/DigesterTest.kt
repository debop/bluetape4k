package io.bluetape4k.io.cryptography.digest

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.junit5.params.provider.FieldSource
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8Bytes
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments

class DigesterTest {

    companion object: KLogging() {
        private fun getRandomString() =
            Fakers.randomString(256, 4096, true)

        private const val REPEAT_SIZE = 10
    }

    private val digesters: List<Arguments> = listOf(
        Digesters.KECCAK256,
        Digesters.KECCAK384,
        Digesters.KECCAK512,
        Digesters.MD5,
        Digesters.SHA1,
        Digesters.SHA256,
        Digesters.SHA384,
        Digesters.SHA512
    ).map { Arguments.of(it) }

    @ParameterizedTest(name = "digest byte array. digester={0}")
    @FieldSource("digesters")
    fun `digest message as byte array`(digester: Digester) {
        repeat(REPEAT_SIZE) {
            val message = getRandomString().toUtf8Bytes()
            val digested = digester.digest(message)
            digester.matches(message, digested).shouldBeTrue()
        }
    }

    @ParameterizedTest(name = "digest string. digester={0}")
    @FieldSource("digesters")
    fun `digest message as string`(digester: Digester) {
        repeat(REPEAT_SIZE) {
            val message = getRandomString()
            val digested = digester.digest(message)
            digester.matches(message, digested).shouldBeTrue()
        }
    }

    @ParameterizedTest(name = "digest char array. digester={0}")
    @FieldSource("digesters")
    fun `digest message with char array`(digester: Digester) {
        log.debug { "Digest CharArray with ${digester.javaClass.simpleName}" }

        repeat(REPEAT_SIZE) {
            val message = getRandomString().toCharArray()
            val digested = digester.digest(message)
            digester.matches(message, digested).shouldBeTrue()
        }
    }
}
