package io.bluetape4k.okio.encrypt

import io.bluetape4k.cryptography.encrypt.Encryptor
import io.bluetape4k.cryptography.encrypt.Encryptors
import io.bluetape4k.logging.KLogging
import io.bluetape4k.okio.AbstractOkioTest
import net.datafaker.Faker
import java.util.*

abstract class AbstractEncryptTest: AbstractOkioTest() {

    companion object: KLogging() {
        const val REPEAT_SIZE = 5

        @JvmStatic
        val faker = Faker(Locale.getDefault())
    }

    fun getEncryptors(): List<Encryptor> = listOf(
        Encryptors.AES,
        Encryptors.DES,
        Encryptors.TripleDES,
        Encryptors.RC2,
        Encryptors.RC4
    )
}
