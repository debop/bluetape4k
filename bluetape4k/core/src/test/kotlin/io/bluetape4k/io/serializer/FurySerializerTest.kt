package io.bluetape4k.io.serializer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class FurySerializerTest {

    companion object: KLogging()

    @Test
    fun `bigdecimal bytes size`() {
        val bigdecimal = BigDecimal.valueOf(1234567890123456789L, 2)
        val bytes = bigdecimal.unscaledValue().toByteArray()

        log.debug { "bytes size: ${bytes.size}" }
    }

    @Nested
    inner class FurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.Fury
    }

    @Nested
    inner class BZip2FurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.BZip2Fury
    }

    @Nested
    inner class DeflateFurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.DeflateFury
    }

    @Nested
    inner class GZipFurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.GZipFury
    }

    @Nested
    inner class LZ4FurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.LZ4Fury
    }

    @Nested
    inner class SnappyFurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.SnappyFury
    }

    @Nested
    inner class ZstdFurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.ZstdFury
    }
}
