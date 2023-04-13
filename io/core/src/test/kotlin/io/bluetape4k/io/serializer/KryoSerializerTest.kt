package io.bluetape4k.io.serializer

import org.junit.jupiter.api.Nested

class KryoSerializerTest {

    @Nested
    inner class KryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.Kryo
    }

    @Nested
    inner class BZip2KryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.BZip2Kryo
    }

    @Nested
    inner class DeflateKryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.DeflateKryo
    }

    @Nested
    inner class GZipKryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.GZipKryo
    }

    @Nested
    inner class LZ4KryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.LZ4Kryo
    }

    @Nested
    inner class SnappyKryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.SnappyKryo
    }

    @Nested
    inner class ZstdKryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.ZstdKryo
    }
}
