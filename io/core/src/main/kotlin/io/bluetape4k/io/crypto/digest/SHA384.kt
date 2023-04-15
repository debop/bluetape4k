package io.bluetape4k.io.crypto.digest

import io.bluetape4k.io.crypto.zeroSaltGenerator
import io.bluetape4k.logging.KLogging
import org.jasypt.salt.SaltGenerator

/**
 * SHA-384 알고리즘을 이용한 [Digester]
 *
 * @param saltGenerator salt generator
 */
class SHA384(saltGenerator: SaltGenerator = zeroSaltGenerator): AbstractDigester(ALGORITHM, saltGenerator) {

    companion object: KLogging() {
        const val ALGORITHM = "SHA-384"
    }
}
