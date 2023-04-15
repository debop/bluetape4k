package io.bluetape4k.io.crypto.digest

import io.bluetape4k.io.crypto.registBouncCastleProvider
import io.bluetape4k.io.crypto.zeroSaltGenerator
import org.jasypt.digest.PooledByteDigester
import org.jasypt.salt.SaltGenerator

/**
 * [Digester]의 최상위 추상화 클래스
 */
abstract class AbstractDigester protected constructor(
    override val algorithm: String,
    override val saltGenerator: SaltGenerator = zeroSaltGenerator,
): Digester {

    private val digester: PooledByteDigester =
        PooledByteDigester().apply {
            registBouncCastleProvider()
            setPoolSize(4)
            setAlgorithm(algorithm)
            setSaltGenerator(saltGenerator)
        }

    override fun digest(message: ByteArray): ByteArray =
        digester.digest(message)

    override fun matches(message: ByteArray, digest: ByteArray): Boolean =
        digester.matches(message, digest)
}
