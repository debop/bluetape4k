package io.bluetape4k.io.serializer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.junit.jupiter.api.DisplayName
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
    @DisplayName("Fury")
    inner class FurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.Fury
    }

    @Nested
    @DisplayName("Fury + BZip2")
    inner class BZip2FurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.BZip2Fury
    }

    @Nested
    @DisplayName("Fury + Deflate")
    inner class DeflateFurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.DeflateFury
    }

    @Nested
    @DisplayName("Fury + GZip")
    inner class GZipFurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.GZipFury
    }

    @Nested
    @DisplayName("Fury + LZ4")
    inner class LZ4FurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.LZ4Fury
    }

    @Nested
    @DisplayName("Fury + Snappy")
    inner class SnappyFurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.SnappyFury
    }

    @Nested
    @DisplayName("Fury + Zstd")
    inner class ZstdFurySerializerTest: AbstractBinarySerializerTest() {
        override val serializer: BinarySerializer = BinarySerializers.ZstdFury
    }
}
