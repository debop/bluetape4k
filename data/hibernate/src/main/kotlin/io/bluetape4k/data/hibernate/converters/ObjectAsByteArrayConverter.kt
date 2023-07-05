package io.bluetape4k.data.hibernate.converters

import io.bluetape4k.codec.decodeBase64ByteArray
import io.bluetape4k.codec.encodeBase64ByteArray
import io.bluetape4k.io.serializer.BinarySerializer
import io.bluetape4k.io.serializer.BinarySerializers
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * 객체를 직렬화하여 Base64 인코딩을 거쳐 ByteArray 로 변환해서 DB에 저장합니다.
 *
 * @property serializer
 * @constructor Create empty Abstract object as byte array converter
 */
abstract class AbstractObjectAsByteArrayConverter(
    private val serializer: BinarySerializer,
): AttributeConverter<Any?, ByteArray?> {

    override fun convertToDatabaseColumn(attribute: Any?): ByteArray? {
        return attribute?.run { serializer.serialize(this).encodeBase64ByteArray() }
    }

    override fun convertToEntityAttribute(dbData: ByteArray?): Any? {
        return dbData?.run { serializer.deserialize(this.decodeBase64ByteArray()) }
    }
}

@Converter
class JdkObjectAsByteArrayConverter: AbstractObjectAsByteArrayConverter(BinarySerializers.Jdk)

@Converter
class KryoObjectAsByteArrayConverter: AbstractObjectAsByteArrayConverter(BinarySerializers.Kryo)

@Converter
class MarshallingObjectAsByteArrayConverter: AbstractObjectAsByteArrayConverter(BinarySerializers.Marshalling)


@Converter
class LZ4KryoObjectAsByteArrayConverter: AbstractObjectAsByteArrayConverter(BinarySerializers.LZ4Kryo)

@Converter
class SnappyKryoObjectAsByteArrayConverter: AbstractObjectAsByteArrayConverter(BinarySerializers.SnappyKryo)

@Converter
class LZ4MarshallingObjectAsByteArrayConverter: AbstractObjectAsByteArrayConverter(BinarySerializers.LZ4Marshalling)

@Converter
class SnappyMarshallingObjectAsByteArrayConverter:
    AbstractObjectAsByteArrayConverter(BinarySerializers.SnappyMarshalling)
