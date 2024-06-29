package io.bluetape4k.okio.cipher

import io.bluetape4k.logging.KLogging
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 암호화/복호화를 위한 [Cipher]를 빌드합니다.
 */
class CipherBuilder {

    companion object: KLogging() {
        const val ALGORITHM_AES = "AES"
        const val TRANSFORMATION_AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding"
        const val DEFAULT_KEY_SIZE = 16

        private val secureRandom = SecureRandom()

        private fun getSecretKey(size: Int = DEFAULT_KEY_SIZE): ByteArray {
            return ByteArray(size).apply {
                secureRandom.nextBytes(this)
            }
        }

        private fun getIvBytes(size: Int = DEFAULT_KEY_SIZE): ByteArray {
            return ByteArray(size).apply {
                secureRandom.nextBytes(this)
            }
        }
    }

    private var secretKey: ByteArray = getSecretKey()
    private var ivBytes: ByteArray = getIvBytes()
    private var algorithm: String = ALGORITHM_AES
    private var transformantion: String = TRANSFORMATION_AES_CBC_PKCS5PADDING

    fun secretKeySize(size: Int = DEFAULT_KEY_SIZE) = apply {
        secretKey(getSecretKey(size))
    }

    fun secretKey(secretKey: ByteArray): CipherBuilder = apply {
        this.secretKey = secretKey
    }

    fun ivBytesSize(size: Int = DEFAULT_KEY_SIZE) = apply {
        ivBytes(getIvBytes(size))
    }

    fun ivBytes(ivBytes: ByteArray): CipherBuilder = apply {
        this.ivBytes = ivBytes
    }

    fun algorithm(algorithm: String = ALGORITHM_AES): CipherBuilder = apply {
        this.algorithm = algorithm
    }

    fun transformation(transformation: String = TRANSFORMATION_AES_CBC_PKCS5PADDING): CipherBuilder = apply {
        this.transformantion = transformation
    }

    private val secretKeySpec: SecretKeySpec
        get() = SecretKeySpec(secretKey, algorithm)

    private val iv: IvParameterSpec
        get() = IvParameterSpec(ivBytes)

    /**
     * 암호화/복호화용 Cipher를 생성합니다.
     *
     * ```
     * // Build an AES cipher for encryption
     * val cipher = CipherBuilder()
     *    .secretKeySize(16)
     *    .ivBytesSize(16)
     *    .algorithm("AES")
     *    .transformation("AES/CBC/PKCS5Padding")
     *    .build(Cipher.ENCRYPT_MODE)
     * ```
     *
     * ```
     * // Build an AES cipher for decryption
     * val decipher = CipherBuilder()
     *      .secretKeySize(16)
     *      .ivBytesSize(16)
     *      .algorithm("AES")
     *      .transformation("AES/CBC/PKCS5Padding")
     *      .build(Cipher.DECRYPT_MODE)
     * ```
     */
    fun build(cipherMode: Int): Cipher {
        return Cipher.getInstance(transformantion).also { cipher ->
            cipher.init(cipherMode, secretKeySpec, iv)
        }
    }
}
