package io.bluetape4k.infra.kafka.codec

import io.bluetape4k.io.serializer.BinarySerializer
import io.bluetape4k.io.serializer.BinarySerializers
import org.apache.kafka.common.header.Headers

abstract class BinaryKafkaCodec<T: Any>(
    private val serializer: BinarySerializer,
): AbstractKafkaCodec<T>() {

    override fun doSerialize(topic: String?, headers: Headers?, graph: T): ByteArray? {
        return serializer.serialize(graph)
    }

    override fun doDeserialize(topic: String?, headers: Headers?, bytes: ByteArray): T? {
        return serializer.deserialize(bytes)
    }
}

class JdkKafkaCodec<T: Any>: BinaryKafkaCodec<T>(BinarySerializers.Jdk)
class Kryo5KafkaCodec<T: Any>: BinaryKafkaCodec<T>(BinarySerializers.Kryo)

class LZ4JdkKafkaCodec<T: Any>: BinaryKafkaCodec<T>(BinarySerializers.LZ4Jdk)
class LZ4Kryo5KafkaCodec<T: Any>: BinaryKafkaCodec<T>(BinarySerializers.LZ4Kryo)

class SnappyJdkKafkaCodec<T: Any>: BinaryKafkaCodec<T>(BinarySerializers.SnappyKryo)
class SnappyKryo5KafkaCodec<T: Any>: BinaryKafkaCodec<T>(BinarySerializers.SnappyKryo)
