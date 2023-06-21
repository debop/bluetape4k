package io.bluetape4k.infra.kafka.codec

import io.bluetape4k.io.serializer.BinarySerializer
import io.bluetape4k.io.serializer.BinarySerializers
import org.apache.kafka.common.header.Headers

abstract class BinaryKafkaCodec(
    private val serializer: BinarySerializer,
): AbstractKafkaCodec<Any?>() {

    override fun doSerialize(topic: String?, headers: Headers?, graph: Any?): ByteArray {
        return serializer.serialize(graph)
    }

    override fun doDeserialize(topic: String?, headers: Headers?, bytes: ByteArray): Any? {
        return serializer.deserialize(bytes)
    }
}

class JdkKafkaCodec: BinaryKafkaCodec(BinarySerializers.Jdk)
class KryoKafkaCodec: BinaryKafkaCodec(BinarySerializers.Kryo)

class LZ4JdkKafkaCodec: BinaryKafkaCodec(BinarySerializers.LZ4Jdk)
class LZ4KryoKafkaCodec: BinaryKafkaCodec(BinarySerializers.LZ4Kryo)

class SnappyJdkKafkaCodec: BinaryKafkaCodec(BinarySerializers.SnappyJdk)
class SnappyKryoKafkaCodec: BinaryKafkaCodec(BinarySerializers.SnappyKryo)

class ZstdJdkKafkaCodec: BinaryKafkaCodec(BinarySerializers.ZstdJdk)
class ZstdKryoKafkaCodec: BinaryKafkaCodec(BinarySerializers.ZstdKryo)
