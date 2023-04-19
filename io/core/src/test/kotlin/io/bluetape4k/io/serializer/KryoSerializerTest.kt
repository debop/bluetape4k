package io.bluetape4k.io.serializer

import org.junit.jupiter.api.Nested

class KryoSerializerTest {

    @Nested
    inner class KryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.Kryo
    }

    @Nested
    inner class BZip2KryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.BZip2Kryo
    }

    @Nested
    inner class DeflateKryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.DeflateKryo
    }

    @Nested
    inner class GZipKryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.GZipKryo
    }

    @Nested
    inner class LZ4KryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.LZ4Kryo
    }

    @Nested
    inner class SnappyKryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.SnappyKryo
    }

    @Nested
    inner class ZstdKryoSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.ZstdKryo
    }
}
