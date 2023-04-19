package io.bluetape4k.data.redis.lettuce.codec

import io.bluetape4k.io.compressor.Compressor
import io.bluetape4k.io.compressor.Compressors
import io.bluetape4k.io.protobuf.serializers.ProtobufSerializer
import io.bluetape4k.io.serializer.BinarySerializer
import io.bluetape4k.io.serializer.CompressableBinarySerializer
import io.bluetape4k.io.serializer.Serializers

object LettuceBinaryCodecs {

    val Default: LettuceBinaryCodec<Any> by lazy { lz4Kryo() }

    fun <V: Any> codec(serializer: BinarySerializer): LettuceBinaryCodec<V> =
        LettuceBinaryCodec(serializer)

    fun <V: Any> compressedCodec(comressor: Compressor, serializer: BinarySerializer): LettuceBinaryCodec<V> =
        LettuceBinaryCodec(CompressableBinarySerializer(serializer, comressor))

    private val protobufSerializer by lazy { ProtobufSerializer() }

    fun <V: Any> jdk(): LettuceBinaryCodec<V> = codec(Serializers.Jdk)

    //    fun <V: Any> fst(): LettuceBinaryCodec<V> = codec(Serializers.Fst)
    fun <V: Any> kryo(): LettuceBinaryCodec<V> = codec(Serializers.Kryo)
    fun <V: Any> marshalling(): LettuceBinaryCodec<V> = codec(Serializers.Marshalling)
    fun <V: Any> protobuf(): LettuceBinaryCodec<V> = codec(ProtobufSerializer())

    fun <V: Any> gzipJdk(): LettuceBinaryCodec<V> = codec(Serializers.GZipJdk)

    //    fun <V: Any> gzipFst(): LettuceBinaryCodec<V> = codec(Serializers.GZipFst)
    fun <V: Any> gzipKryo(): LettuceBinaryCodec<V> = codec(Serializers.GZipKryo)
    fun <V: Any> gzipMarshalling(): LettuceBinaryCodec<V> = codec(Serializers.GZipMarshalling)
    fun <V: Any> gzipProtobuf(): LettuceBinaryCodec<V> = compressedCodec(Compressors.GZip, protobufSerializer)

    fun <V: Any> deflateJdk(): LettuceBinaryCodec<V> = codec(Serializers.DeflateJdk)

    //    fun <V: Any> deflateFst(): LettuceBinaryCodec<V> = codec(Serializers.DeflateFst)
    fun <V: Any> deflateKryo(): LettuceBinaryCodec<V> = codec(Serializers.DeflateKryo)
    fun <V: Any> deflateMarshalling(): LettuceBinaryCodec<V> = codec(Serializers.DeflateMarshalling)
    fun <V: Any> deflateProtobuf(): LettuceBinaryCodec<V> = compressedCodec(Compressors.Deflate, protobufSerializer)

    fun <V: Any> snappyJdk(): LettuceBinaryCodec<V> = codec(Serializers.SnappyJdk)

    //    fun <V: Any> snappyFst(): LettuceBinaryCodec<V> = codec(Serializers.SnappyFst)
    fun <V: Any> snappyKryo(): LettuceBinaryCodec<V> = codec(Serializers.SnappyKryo)
    fun <V: Any> snappyMarshalling(): LettuceBinaryCodec<V> = codec(Serializers.SnappyMarshalling)
    fun <V: Any> snappyProtobuf(): LettuceBinaryCodec<V> = compressedCodec(Compressors.Snappy, protobufSerializer)

    fun <V: Any> lz4Jdk(): LettuceBinaryCodec<V> = codec(Serializers.LZ4Jdk)

    //    fun <V: Any> lz4Fst(): LettuceBinaryCodec<V> = codec(Serializers.LZ4Fst)
    fun <V: Any> lz4Kryo(): LettuceBinaryCodec<V> = codec(Serializers.LZ4Kryo)
    fun <V: Any> lz4Marshalling(): LettuceBinaryCodec<V> = codec(Serializers.LZ4Marshalling)
    fun <V: Any> lz4Protobuf(): LettuceBinaryCodec<V> = compressedCodec(Compressors.LZ4, protobufSerializer)

    fun <V: Any> zstdJdk(): LettuceBinaryCodec<V> = codec(Serializers.ZstdJdk)

    //    fun <V: Any> zstdFst(): LettuceBinaryCodec<V> = codec(Serializers.ZstdFst)
    fun <V: Any> zstdKryo(): LettuceBinaryCodec<V> = codec(Serializers.ZstdKryo)
    fun <V: Any> zstdMarshalling(): LettuceBinaryCodec<V> = codec(Serializers.ZstdMarshalling)
    fun <V: Any> zstdProtobuf(): LettuceBinaryCodec<V> = compressedCodec(Compressors.Zstd, protobufSerializer)
}
