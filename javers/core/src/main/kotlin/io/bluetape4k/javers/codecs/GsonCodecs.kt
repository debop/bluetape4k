package io.bluetape4k.javers.codecs

import io.bluetape4k.io.compressor.Compressors
import io.bluetape4k.io.serializer.BinarySerializers

object GsonCodecs {

    val Default by lazy { String }

    // String Codecs

    val String by lazy { StringGsonCodec() }

    val GZipString by lazy { CompressableStringGsonCodec(String, Compressors.GZip) }
    val DeflateString by lazy { CompressableStringGsonCodec(String, Compressors.Deflate) }
    val LZ4String by lazy { CompressableStringGsonCodec(String, Compressors.LZ4) }
    val SnappyString by lazy { CompressableStringGsonCodec(String, Compressors.Snappy) }
    val ZstdString by lazy { CompressableStringGsonCodec(String, Compressors.Zstd) }

    // Binary Codecs

    val Kryo by lazy { BinaryGsonCodec(BinarySerializers.Kryo) }

    val GZipKryo by lazy { CompressableBinaryGsonCodec(Kryo, Compressors.GZip) }
    val DeflateKryo by lazy { CompressableBinaryGsonCodec(Kryo, Compressors.Deflate) }
    val LZ4Kryo by lazy { CompressableBinaryGsonCodec(Kryo, Compressors.LZ4) }
    val SnappyKryo by lazy { CompressableBinaryGsonCodec(Kryo, Compressors.Snappy) }
    val ZstdKryo by lazy { CompressableBinaryGsonCodec(Kryo, Compressors.Zstd) }

    // Map
    val Map by lazy { MapGsonCodec() }
}
