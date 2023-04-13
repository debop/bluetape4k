package io.bluetape4k.io.serializer

import org.junit.jupiter.api.Nested

class JdkSerializerTest {

    @Nested
    inner class JdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.Jdk
    }

    @Nested
    inner class BZip2JdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.BZip2Jdk
    }

    @Nested
    inner class DeflateJdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.DeflateJdk
    }

    @Nested
    inner class GZipJdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.GZipJdk
    }

    @Nested
    inner class LZ4JdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.LZ4Jdk
    }

    @Nested
    inner class SnappyJdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.SnappyJdk
    }

    @Nested
    inner class ZstdJdkSerializerTest : AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = Serializers.ZstdJdk
    }
}
