package io.bluetape4k.io.compressor

import io.bluetape4k.support.unsafeLazy

/**
 * Compressors
 */
object Compressors {

    /**
     * [BZip2Compressor]
     */
    val BZip2: BZip2Compressor by unsafeLazy { BZip2Compressor() }

    /**
     * [DeflateCompressor]
     */
    val Deflate: DeflateCompressor by unsafeLazy { DeflateCompressor() }

    /**
     * [GZipCompressor]
     */
    val GZip: GZipCompressor by unsafeLazy { GZipCompressor() }

    /**
     * [LZ4Compressor]
     */
    val LZ4: LZ4Compressor by unsafeLazy { LZ4Compressor() }

    /**
     * [SnappyCompressor]
     */
    val Snappy: SnappyCompressor by unsafeLazy { SnappyCompressor() }

    /**
     * [XZCompressor]
     */
    val XZ: XZCompressor by unsafeLazy { XZCompressor() }

    /**
     * [ZstdCompressor]
     */
    val Zstd: ZstdCompressor by unsafeLazy { ZstdCompressor() }

    /**
     * [BrotliCompressor]
     */
    val Brotli: BrotliCompressor by unsafeLazy { BrotliCompressor() }
}
