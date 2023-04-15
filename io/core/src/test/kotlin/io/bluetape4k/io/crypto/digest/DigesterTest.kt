package io.bluetape4k.io.crypto.digest

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.junit5.params.provider.FieldSource
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8Bytes
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments

class DigesterTest {

    companion object: KLogging() {
        private fun getRandomString() =
            Fakers.randomString(100, 4096, true)

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

    @ParameterizedTest
    @FieldSource("digesters")
    fun `digest message as byte array`(digester: Digester) {
        repeat(REPEAT_SIZE) {
            val message = getRandomString().toUtf8Bytes()
            val digested = digester.digest(message)
            digester.matches(message, digested).shouldBeTrue()
        }
    }

    @ParameterizedTest
    @FieldSource("digesters")
    fun `digest message as string`(digester: Digester) {
        repeat(REPEAT_SIZE) {
            val message = getRandomString()
            val digested = digester.digest(message)
            digester.matches(message, digested).shouldBeTrue()
        }
    }
}
