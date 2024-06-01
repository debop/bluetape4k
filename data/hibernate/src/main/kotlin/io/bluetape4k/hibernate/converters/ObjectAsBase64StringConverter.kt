package io.bluetape4k.hibernate.converters

import io.bluetape4k.codec.decodeBase64ByteArray
import io.bluetape4k.codec.encodeBase64String
import io.bluetape4k.io.serializer.BinarySerializer
import io.bluetape4k.io.serializer.BinarySerializers
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter


/**
 * 객체를 직렬화하여 Base64 인코딩된 문자열로 변환해서 DB에 저장합니다.
 *
 * @property serializer
 * @constructor Create empty Abstract object as byte array converter
 */
abstract class AbstractObjectAsBase64StringConverter(
    private val serializer: BinarySerializer,
): AttributeConverter<Any?, String?> {

    override fun convertToDatabaseColumn(attribute: Any?): String? {
        return attribute?.run { serializer.serialize(this).encodeBase64String() }
    }

    override fun convertToEntityAttribute(dbData: String?): Any? {
        return dbData?.run { serializer.deserialize(this.decodeBase64ByteArray()) }
    }
}


@Converter
class JdkObjectAsBase64StringConverter: AbstractObjectAsBase64StringConverter(BinarySerializers.Jdk)

@Converter
class KryoObjectAsBase64StringConverter: AbstractObjectAsBase64StringConverter(BinarySerializers.Kryo)

@Converter
class MarshallingObjectAsBase64StringConverter: AbstractObjectAsBase64StringConverter(BinarySerializers.Marshalling)


@Converter
class LZ4KryoObjectAsBase64StringConverter: AbstractObjectAsBase64StringConverter(BinarySerializers.LZ4Kryo)

@Converter
class SnappyKryoObjectAsBase64StringConverter: AbstractObjectAsBase64StringConverter(BinarySerializers.SnappyKryo)

@Converter
class LZ4MarshallingObjectAsBase64StringConverter:
    AbstractObjectAsBase64StringConverter(BinarySerializers.LZ4Marshalling)

@Converter
class SnappyMarshallingObjectAsBase64StringConverter:
    AbstractObjectAsBase64StringConverter(BinarySerializers.SnappyMarshalling)
