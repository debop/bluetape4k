package io.bluetape4k.io.crypto

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.info
import io.bluetape4k.support.emptyByteArray
import java.security.SecureRandom
import java.security.Security
import java.util.Random
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.jasypt.salt.ZeroSaltGenerator

private val log = KotlinLogging.logger {}

/**
 * 난수 발생 기본 알고리즘
 */
internal const val randomNumberGenerationAlgorithm = "SHA1PRNG"

/**
 * [ZeroSaltGenerator] 인스턴스
 */
@JvmField
internal val zeroSaltGenerator = ZeroSaltGenerator()

@JvmField
internal val secureRandom: Random = SecureRandom.getInstance(randomNumberGenerationAlgorithm)

/**
 * Random 값을 가지는 [ByteArray]를 빌드합니다.
 *
 * @param size byte array 크기 (0보다 작으면 empty byte array를 반환)
 * @return random 값을 가지는 byte array
 */
fun randomBytes(size: Int): ByteArray {
    if (size <= 0) {
        return emptyByteArray
    }

    return ByteArray(size).apply { secureRandom.nextBytes(this) }
}

@Synchronized
internal fun registBouncCastleProvider() {
    if (Security.getProvider("BC") == null) {
        log.info { "Add BouncyCastle Provider" }
        runCatching { Security.addProvider(BouncyCastleProvider()) }
    }
}
