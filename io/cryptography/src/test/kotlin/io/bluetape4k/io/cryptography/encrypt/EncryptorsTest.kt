package io.bluetape4k.io.cryptography.encrypt

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.support.LINE_SEPARATOR
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Test

class EncryptorsTest {

    companion object: KLogging()

    @Test
    fun `get all pbe algorithms`() {
        val algorithms = Encryptors.getAlgorithmes()
        log.info { algorithms.joinToString(LINE_SEPARATOR) }

        algorithms shouldContain AES.ALGORITHM.uppercase()
        algorithms shouldContain DES.ALGORITHM.uppercase()
        algorithms shouldContain RC2.ALGORITHM.uppercase()
        algorithms shouldContain RC4.ALGORITHM.uppercase()
        algorithms shouldContain TripleDES.ALGORITHM.uppercase()
    }
}
