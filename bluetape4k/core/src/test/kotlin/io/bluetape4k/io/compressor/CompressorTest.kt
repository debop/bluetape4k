package io.bluetape4k.io.compressor

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

class CompressorTest {

    @Nested
    @DisplayName("BZip2")
    inner class BZip2CompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = BZip2Compressor()
    }

    @Nested
    @DisplayName("Deflate")
    inner class DeflateCompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = DeflateCompressor()
    }

    @Nested
    @DisplayName("GZip")
    inner class GZipCompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = GZipCompressor()
    }

    @Nested
    @DisplayName("LZ4")
    inner class LZ4CompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = LZ4Compressor()
    }

    @Nested
    @DisplayName("Snappy")
    inner class SnappyCompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = SnappyCompressor()
    }

    @Nested
    @DisplayName("XZ")
    inner class XZCompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = XZCompressor()
    }

    @Nested
    @DisplayName("Zstd")
    inner class ZstdCompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = ZstdCompressor()
    }

    @Nested
    @DisplayName("BlockLZ4")
    inner class BlockLZ4CompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = BlockLZ4Compressor()
    }

    @Nested
    @DisplayName("FramedLZ4")
    inner class FramedLZ4CompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = FramedLZ4Compressor()
    }

    @Nested
    @DisplayName("Framed Snappy")
    inner class FramedSnappyCompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = FramedSnappyCompressor()
    }

    @Nested
    @DisplayName("LZMA")
    inner class LZMACompressorTest: AbstractCompressorTest() {
        override val compressor: Compressor = LZMACompressor()
    }
}
