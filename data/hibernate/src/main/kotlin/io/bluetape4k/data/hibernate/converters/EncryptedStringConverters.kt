package io.bluetape4k.data.hibernate.converters

import io.bluetape4k.io.cryptography.encrypt.Encryptor
import io.bluetape4k.io.cryptography.encrypt.Encryptors
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * 문자열을 암호화해서 문자열로 저장하는 JPA Converter 입니다.
 */
abstract class EncryptedStringConverter(
    private val encryptor: Encryptor,
): AttributeConverter<String?, String?> {

    override fun convertToDatabaseColumn(attribute: String?): String? {
        return attribute?.run { encryptor.encrypt(this) }
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        return dbData?.run { encryptor.decrypt(this) }
    }
}

@Converter
class AESStringConverter: EncryptedStringConverter(Encryptors.AES)

@Converter
class DESStringConverter: EncryptedStringConverter(Encryptors.DES)

@Converter
class RC2StringConverter: EncryptedStringConverter(Encryptors.RC2)

@Converter
class RC4StringConverter: EncryptedStringConverter(Encryptors.RC4)

@Converter
class TripleDESStringConverter: EncryptedStringConverter(Encryptors.TripleDES)
