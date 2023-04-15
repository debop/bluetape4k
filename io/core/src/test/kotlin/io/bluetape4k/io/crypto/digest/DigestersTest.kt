package io.bluetape4k.io.crypto.digest

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.LINE_SEPARATOR
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Test

class DigestersTest {
    companion object: KLogging()

    @Test
    fun `get all digester algorithmes`() {
        val algorithms = Digesters.getAllDigestAlgorithms()
        log.debug { algorithms.joinToString(LINE_SEPARATOR) }

        algorithms shouldContain Keccak256.ALGORITHM
        algorithms shouldContain Keccak384.ALGORITHM
        algorithms shouldContain Keccak512.ALGORITHM

        algorithms shouldContain MD5.ALGORITHM
        algorithms shouldContain SHA1.ALGORITHM
        algorithms shouldContain SHA256.ALGORITHM
        algorithms shouldContain SHA384.ALGORITHM
        algorithms shouldContain SHA512.ALGORITHM
    }
}
