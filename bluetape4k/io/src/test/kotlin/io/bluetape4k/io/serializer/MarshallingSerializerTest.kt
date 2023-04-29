package io.bluetape4k.io.serializer

import org.junit.jupiter.api.Nested

class MarshallingSerializerTest {

    @Nested
    inner class MarshallingSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.Marshalling
    }

    @Nested
    inner class BZip2MarshallingSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.BZip2Marshalling
    }

    @Nested
    inner class DeflateMarshallingSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.DeflateMarshalling
    }

    @Nested
    inner class GZipMarshallingSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.GZipMarshalling
    }

    @Nested
    inner class LZ4MarshallingSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.LZ4Marshalling
    }

    @Nested
    inner class SnappyMarshallingSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.SnappyMarshalling
    }

    @Nested
    inner class ZstdMarshallingSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.ZstdMarshalling
    }
}
