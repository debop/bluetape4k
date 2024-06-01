package io.bluetape4k.kafka.codec

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
class FuryKafkaCodec: BinaryKafkaCodec(BinarySerializers.Fury)

class LZ4JdkKafkaCodec: BinaryKafkaCodec(BinarySerializers.LZ4Jdk)
class LZ4KryoKafkaCodec: BinaryKafkaCodec(BinarySerializers.LZ4Kryo)
class LZ4FuryKafkaCodec: BinaryKafkaCodec(BinarySerializers.LZ4Fury)

class SnappyJdkKafkaCodec: BinaryKafkaCodec(BinarySerializers.SnappyJdk)
class SnappyKryoKafkaCodec: BinaryKafkaCodec(BinarySerializers.SnappyKryo)
class SnappyFuryKafkaCodec: BinaryKafkaCodec(BinarySerializers.SnappyFury)

class ZstdJdkKafkaCodec: BinaryKafkaCodec(BinarySerializers.ZstdJdk)
class ZstdKryoKafkaCodec: BinaryKafkaCodec(BinarySerializers.ZstdKryo)
class ZstdFuryKafkaCodec: BinaryKafkaCodec(BinarySerializers.ZstdFury)
