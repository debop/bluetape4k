package io.bluetape4k.io.cryptography.digest

import io.bluetape4k.io.cryptography.zeroSaltGenerator
import io.bluetape4k.logging.KLogging
import org.jasypt.salt.SaltGenerator

/**
 * KECCAK-384 알고리즘을 이용한 [Digester]
 *
 * @param saltGenerator salt generator
 */
class Keccak384(saltGenerator: SaltGenerator = zeroSaltGenerator): AbstractDigester(ALGORITHM, saltGenerator) {

    companion object: KLogging() {
        const val ALGORITHM = "KECCAK-384"
    }
}
