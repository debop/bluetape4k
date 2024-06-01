package io.bluetape4k.cryptography.encrypt

import io.bluetape4k.cryptography.zeroSaltGenerator
import org.jasypt.salt.SaltGenerator

/**
 * TripleDES 대칭형 알고리즘을 이용한 [Encryptor] 입니다.
 *
 * @param saltGenerator salt generator
 * @param password password
 */
class TripleDES(
    saltGenerator: SaltGenerator = zeroSaltGenerator,
    password: String = DEFAULT_PASSWORD,
): AbstractEncryptor(ALGORITHM, saltGenerator, password) {

    companion object {
        const val ALGORITHM = "PBEWITHSHAAND3-KEYTRIPLEDES-CBC"
    }
}
