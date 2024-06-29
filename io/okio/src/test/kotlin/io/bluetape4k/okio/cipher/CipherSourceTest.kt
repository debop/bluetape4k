package io.bluetape4k.okio.cipher

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.okio.bufferOf
import io.bluetape4k.support.toUtf8Bytes
import okio.Buffer
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class CipherSourceTest: AbstractCipherTest() {

    companion object: KLogging()

    @Test
    fun `read empty source`() {
        val cipheredSource = Buffer()

        val decoded = CipherSource(cipheredSource, decryptCipher)

        val output = bufferOf(decoded)
        output.readUtf8() shouldBeEqualTo ""
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `decrypt by cipher source`() {
        val expected = faker.lorem().paragraph()
        log.debug { "expected=$expected" }

        val encryptedSource = bufferOf(encryptCipher.doFinal(expected.toUtf8Bytes()))
        val decoded = CipherSource(encryptedSource, decryptCipher)
        val output = bufferOf(decoded)

        output.readUtf8() shouldBeEqualTo expected
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `decrypt by cipher source with chunked with Default Locale`() {
        // NOTE Input is longer than 1 cipher block of 16 bytes
        val expected = faker.lorem().paragraph()
        val expectedBytes = expected.toUtf8Bytes()

        val cipheredSource = bufferOf(encryptCipher.doFinal(expectedBytes))
        val decoded = CipherSource(cipheredSource, decryptCipher)

        // First request 5 bytes
        val output = Buffer()
        decoded.read(output, 5)
        output.readByteArray() shouldBeEqualTo expectedBytes.copyOfRange(0, 5)

        decoded.read(output, 10)
        output.readByteArray() shouldBeEqualTo expectedBytes.copyOfRange(5, 15)

        decoded.read(output, 100)
        output.readByteArray() shouldBeEqualTo expectedBytes.copyOfRange(15, 115)
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `read ciphered chunked until stream end`() {
        val expected = faker.lorem().paragraph()
        val expectedBytes = expected.toUtf8Bytes()
        val ciphered = encryptCipher.doFinal(expectedBytes)

        val cipheredSource = bufferOf(ciphered)
        val decoded = CipherSource(cipheredSource, decryptCipher)

        val output = Buffer()
        var readBytesCount = Long.MAX_VALUE
        while (readBytesCount > 0) {
            readBytesCount = decoded.read(output, 5)
        }

        output.readUtf8() shouldBeEqualTo expected
    }
}
