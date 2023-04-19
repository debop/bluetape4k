package io.bluetape4k.io.serializer

import org.junit.jupiter.api.Nested

class JdkSerializerTest {

    @Nested
    inner class JdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.Jdk
    }

    @Nested
    inner class BZip2JdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.BZip2Jdk
    }

    @Nested
    inner class DeflateJdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.DeflateJdk
    }

    @Nested
    inner class GZipJdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.GZipJdk
    }

    @Nested
    inner class LZ4JdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.LZ4Jdk
    }

    @Nested
    inner class SnappyJdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.SnappyJdk
    }

    @Nested
    inner class ZstdJdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.ZstdJdk
    }
}
