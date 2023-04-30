package io.bluetape4k.io.cryptography.encrypt

import io.bluetape4k.io.cryptography.registBouncCastleProvider
import io.bluetape4k.logging.KLogging
import org.jasypt.registry.AlgorithmRegistry

object Encryptors: KLogging() {

    init {
        registBouncCastleProvider()
    }

    /**
     * AES 알고리즘을 이용한 대칭형 암호기
     */
    val AES by lazy { AES() }

    /**
     * RC2 알고리즘을 이용한 대칭형 암호기
     */
    val RC2 by lazy { RC2() }

    /**
     * RC4 알고리즘을 이용한 대칭형 암호기
     */
    val RC4 by lazy { RC4() }

    /**
     * DES 알고리즘을 이용한 대칭형 암호기
     */
    val DES by lazy { DES() }

    /**
     * TripleDES 알고리즘을 이용한 대칭형 암호기
     */
    val TripleDES by lazy { TripleDES() }


    @Suppress("UNCHECKED_CAST")
    fun getAlgorithmes(): Set<String> {
        /*
            PBEWITHHMACSHA1ANDAES_256
            PBEWITHHMACSHA224ANDAES_128
            PBEWITHHMACSHA224ANDAES_256
            PBEWITHHMACSHA256ANDAES_128
            PBEWITHHMACSHA256ANDAES_256
            PBEWITHHMACSHA384ANDAES_128
            PBEWITHHMACSHA384ANDAES_256
            PBEWITHHMACSHA512ANDAES_128
            PBEWITHHMACSHA512ANDAES_256
            PBEWITHMD5ANDDES
            PBEWITHMD5ANDTRIPLEDES
            PBEWITHSHA1ANDDESEDE
            PBEWITHSHA1ANDRC2_128
            PBEWITHSHA1ANDRC2_40
            PBEWITHSHA1ANDRC4_128
            PBEWITHSHA1ANDRC4_40
         */
        return AlgorithmRegistry.getAllPBEAlgorithms() as Set<String>
    }
}
