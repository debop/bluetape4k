package io.bluetape4k.okio.cipher

import io.bluetape4k.logging.KLogging
import io.bluetape4k.okio.AbstractOkioTest
import org.junit.jupiter.api.BeforeEach
import javax.crypto.Cipher

abstract class AbstractCipherTest: AbstractOkioTest() {

    companion object: KLogging() {
        const val REPEAT_SIZE = 5
    }

    protected val builder = CipherBuilder()
        .secretKeySize(16)
        .ivBytesSize(16)
        .algorithm(CipherBuilder.ALGORITHM_AES)
        .transformation(CipherBuilder.TRANSFORMATION_AES_CBC_PKCS5PADDING)

    protected lateinit var encryptCipher: Cipher
    protected lateinit var decryptCipher: Cipher

    @BeforeEach
    fun beforeEach() {
        encryptCipher = builder.build(Cipher.ENCRYPT_MODE)
        decryptCipher = builder.build(Cipher.DECRYPT_MODE)
    }

}
