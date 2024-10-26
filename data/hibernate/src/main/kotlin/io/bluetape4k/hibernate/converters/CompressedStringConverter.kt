package io.bluetape4k.hibernate.converters

import io.bluetape4k.io.compressor.Compressor
import io.bluetape4k.io.compressor.Compressors
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * 문자열을 압축해서 문자열로 저장하는 JPA Converter 입니다.
 */
abstract class AbstractCompressedStringConverter(
    private val compressor: Compressor,
): AttributeConverter<String?, String?> {

    override fun convertToDatabaseColumn(attribute: String?): String? {
        return attribute?.run { compressor.compress(this) }
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        return dbData?.run { compressor.decompress(this) }
    }

}

@Converter
class BZip2StringConverter: AbstractCompressedStringConverter(Compressors.BZip2)

@Converter
class DeflateStringConverter: AbstractCompressedStringConverter(Compressors.Deflate)

@Converter
class GZipStringConverter: AbstractCompressedStringConverter(Compressors.GZip)

@Converter
class LZ4StringConverter: AbstractCompressedStringConverter(Compressors.LZ4)

@Converter
class SnappyStringConverter: AbstractCompressedStringConverter(Compressors.Snappy)

@Converter
class ZstdStringConverter: AbstractCompressedStringConverter(Compressors.Zstd)
