package io.bluetape4k.io.compressor

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

class CompressorTest {

    @Nested
    @DisplayName("BZip2 Compressor")
    inner class BZip2CompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = BZip2Compressor()
    }

    @Nested
    inner class DeflateCompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = DeflateCompressor()
    }

    @Nested
    inner class GZipCompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = GZipCompressor()
    }

    @Nested
    inner class LZ4CompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = LZ4Compressor()
    }

    @Nested
    inner class SnappyCompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = SnappyCompressor()
    }

    @Nested
    inner class XZCompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = XZCompressor()
    }

    @Nested
    inner class ZstdCompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = ZstdCompressor()
    }
}
