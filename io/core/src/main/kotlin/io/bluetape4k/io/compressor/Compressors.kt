package io.bluetape4k.io.compressor

/**
 * Compressors
 */
object Compressors {

    val BZip2: BZip2Compressor by lazy { BZip2Compressor() }

    val Deflate: DeflateCompressor by lazy { DeflateCompressor() }

    val GZip: GZipCompressor by lazy { GZipCompressor() }

    val LZ4: LZ4Compressor by lazy { LZ4Compressor() }

    val Snappy: SnappyCompressor by lazy { SnappyCompressor() }

    val XZ: XZCompressor by lazy { XZCompressor() }

    val Zstd: ZstdCompressor by lazy { ZstdCompressor() }

    val Brotli: BrotliCompressor by lazy { BrotliCompressor() }
}
