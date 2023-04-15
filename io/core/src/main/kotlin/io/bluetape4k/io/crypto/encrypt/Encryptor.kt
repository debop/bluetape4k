package io.bluetape4k.io.crypto.encrypt

import io.bluetape4k.codec.decodeBase64ByteArray
import io.bluetape4k.codec.decodeBase64String
import io.bluetape4k.codec.encodeBase64ByteArray
import io.bluetape4k.codec.encodeBase64String
import io.bluetape4k.support.EMPTY_STRING
import org.jasypt.salt.SaltGenerator

/**
 * 대칭형 암호 (Symmetric Encryption) 을 수행하는 암호기의 인터페이스입니다.
 */
interface Encryptor {

    /**
     * 대칭형 암호화를 위한 알고리즘 명
     */
    val algorithm: String

    /**
     * 암호화 시 사용할 Salt를 생성하는 생성기
     */
    val saltGenerator: SaltGenerator

    /**
     * 비밀번호
     */
    val password: String

    /**
     * 지정된 일반 바이트 배열 정보를 암호화하여 바이트 배열로 반환합니다.
     * @param message 일반 바이트 배열
     * @return 암호화된 바이트 배열
     */
    fun encrypt(message: ByteArray?): ByteArray

    /**
     * 지정된 문자열을 암호화하여 반환합니다.
     * @param message 암호화할 일반 문자열
     */
    fun encrypt(message: String?): String {
        return message?.run {
            encrypt(encodeBase64ByteArray()).encodeBase64String()
        } ?: EMPTY_STRING
    }

    /**
     * 암호화된 바이트 배열을 복호화하여, 일반 바이트 배열로 반환합니다.
     * @param encrypted 암호화된 바이트 배열
     * @return 복호화한 바이트 배열
     */
    fun decrypt(encrypted: ByteArray?): ByteArray

    /**
     * 암호화된 문자열을 복호화하여 일반 문자열로 반환합니다.
     * @param encrypted 암호화된 문자열
     * @return 복호화된 일반 문자열
     */
    fun decrypt(encrypted: String?): String {
        return encrypted?.run {
            decrypt(decodeBase64ByteArray()).decodeBase64String()
        } ?: EMPTY_STRING
    }

    /**
     * 지정된 CharArray 를 암호화 합니다.
     *
     * @param message 암호화할 CharArray
     * @return 암호화된 CharArray
     */
    fun encrypt(message: CharArray): CharArray =
        encrypt(message.concatToString()).toCharArray()

    /**
     * 암호화된 CharArray을 복호화하여 원문 CharArray로 반환합니다.
     * @param encrypted 암호화된 CharArray
     * @return 복호화된 원문 CharArray
     */
    fun decrypt(encrypted: CharArray): CharArray =
        decrypt(encrypted.concatToString()).toCharArray()
}
