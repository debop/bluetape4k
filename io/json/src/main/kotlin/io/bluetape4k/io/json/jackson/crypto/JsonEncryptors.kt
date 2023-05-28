package io.bluetape4k.io.json.jackson.crypto

import io.bluetape4k.io.cryptography.encrypt.Encryptor
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.newInstanceOrNull
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object JsonEncryptors: KLogging() {

    private val encryptors = ConcurrentHashMap<KClass<*>, Encryptor>()

    fun getEncryptor(encryptorType: KClass<out Encryptor>): Encryptor {
        return encryptors.getOrPut(encryptorType) {
            encryptorType.newInstanceOrNull()!!
        }
    }
}
