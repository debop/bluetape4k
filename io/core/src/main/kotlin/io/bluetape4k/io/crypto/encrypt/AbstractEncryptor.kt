package io.bluetape4k.io.crypto.encrypt

import io.bluetape4k.io.crypto.registBouncCastleProvider
import io.bluetape4k.io.crypto.zeroSaltGenerator
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.emptyByteArray
import org.jasypt.encryption.pbe.PooledPBEByteEncryptor
import org.jasypt.salt.SaltGenerator

/**
 * [Encryptor] 의 추상 클래스입니다.
 *
 * @author debop
 */
abstract class AbstractEncryptor @JvmOverloads protected constructor(
    override val algorithm: String,
    override val saltGenerator: SaltGenerator = zeroSaltGenerator,
    override val password: String = DEFAULT_PASSWORD,
): Encryptor {

    protected companion object: KLogging()

    private val encryptor: PooledPBEByteEncryptor =
        PooledPBEByteEncryptor().apply {
            registBouncCastleProvider()
            setPoolSize(4)
            setAlgorithm(algorithm)
            setSaltGenerator(saltGenerator)
            setPassword(password)
        }

    /**
     * 지정된 일반 바이트 배열 정보를 암호화하여 바이트 배열로 반환합니다.
     * @param message 일반 바이트 배열
     * @return 암호화된 바이트 배열
     */
    override fun encrypt(message: ByteArray?): ByteArray {
        return message?.run { encryptor.encrypt(this) } ?: emptyByteArray
    }

    /**
     * 암호화된 바이트 배열을 복호화하여, 일반 바이트 배열로 반환합니다.
     * @param encrypted 암호화된 바이트 배열
     * @return 복호화한 바이트 배열
     */
    override fun decrypt(encrypted: ByteArray?): ByteArray {
        return encrypted?.run { encryptor.decrypt(this) } ?: emptyByteArray
    }
}
