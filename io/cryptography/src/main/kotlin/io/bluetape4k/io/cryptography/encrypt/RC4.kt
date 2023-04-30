package io.bluetape4k.io.cryptography.encrypt

import io.bluetape4k.io.cryptography.zeroSaltGenerator
import org.jasypt.salt.SaltGenerator

/**
 * RC4 대칭형 알고리즘을 이용한 [Encryptor] 입니다.
 *
 * @param saltGenerator salt generator
 * @param password password
 */
class RC4(
    saltGenerator: SaltGenerator = zeroSaltGenerator,
    password: String = DEFAULT_PASSWORD,
): AbstractEncryptor(ALGORITHM, saltGenerator, password) {

    companion object {
        const val ALGORITHM = "PBEWITHSHAAND128BITRC4"
    }
}
