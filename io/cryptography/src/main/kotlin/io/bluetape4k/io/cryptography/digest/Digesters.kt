package io.bluetape4k.io.cryptography.digest

import io.bluetape4k.io.cryptography.registBouncCastleProvider
import io.bluetape4k.logging.KLogging
import org.jasypt.registry.AlgorithmRegistry

/**
 * Hash 알고리즘을 활용한 Digesters
 */
object Digesters: KLogging() {

    init {
        registBouncCastleProvider()
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllDigestAlgorithms(): Set<String> {
        return AlgorithmRegistry.getAllDigestAlgorithms() as Set<String>
    }


    /**
     * Keccak 256 알고리즘을 활용한 Digester
     */
    val KECCAK256 by lazy { Keccak256() }

    /**
     * Keccak 384 알고리즘을 활용한 Digester
     */
    val KECCAK384 by lazy { Keccak384() }

    /**
     * Keccak 512 알고리즘을 활용한 Digester
     */
    val KECCAK512 by lazy { Keccak512() }

    /**
     * MD5 알고리즘을 활용한 Digester
     */
    val MD5 by lazy { MD5() }

    /**
     * SHA-1 알고리즘을 활용한 Digester
     */
    val SHA1 by lazy { SHA1() }

    /**
     * SHA-256 알고리즘을 활용한 Digester
     */
    val SHA256 by lazy { SHA256() }

    /**
     * SHA-384 알고리즘을 활용한 Digester
     */
    val SHA384 by lazy { SHA384() }

    /**
     * SHA-512 알고리즘을 활용한 Digester
     */
    val SHA512 by lazy { SHA512() }

}
