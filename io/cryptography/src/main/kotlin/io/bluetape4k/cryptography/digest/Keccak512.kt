package io.bluetape4k.cryptography.digest

import io.bluetape4k.cryptography.zeroSaltGenerator
import io.bluetape4k.logging.KLogging
import org.jasypt.salt.SaltGenerator

/**
 * KECCAK-512 알고리즘을 이용한 [Digester]
 *
 * @param saltGenerator salt generator
 */
class Keccak512(saltGenerator: SaltGenerator = zeroSaltGenerator): AbstractDigester(ALGORITHM, saltGenerator) {

    companion object: KLogging() {
        const val ALGORITHM = "KECCAK-512"
    }
}
