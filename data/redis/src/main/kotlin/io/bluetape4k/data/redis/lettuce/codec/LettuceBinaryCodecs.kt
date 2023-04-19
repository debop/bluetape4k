package io.bluetape4k.data.redis.lettuce.codec

import io.bluetape4k.io.compressor.Compressor
import io.bluetape4k.io.compressor.Compressors
import io.bluetape4k.io.protobuf.serializers.ProtobufSerializer
import io.bluetape4k.io.serializer.BinarySerializer
import io.bluetape4k.io.serializer.BinarySerializers
import io.bluetape4k.io.serializer.CompressableBinarySerializer

object LettuceBinaryCodecs {

    val Default: LettuceBinaryCodec<Any> by lazy { lz4Kryo() }

    fun <V: Any> codec(serializer: BinarySerializer): LettuceBinaryCodec<V> =
        LettuceBinaryCodec(serializer)

    fun <V: Any> compressedCodec(comressor: Compressor, serializer: BinarySerializer): LettuceBinaryCodec<V> =
        LettuceBinaryCodec(CompressableBinarySerializer(serializer, comressor))

    private val protobufSerializer by lazy { ProtobufSerializer() }

    fun <V: Any> jdk(): LettuceBinaryCodec<V> = codec(BinarySerializers.Jdk)

    //    fun <V: Any> fst(): LettuceBinaryCodec<V> = codec(BinarySerializers.Fst)
    fun <V: Any> kryo(): LettuceBinaryCodec<V> = codec(BinarySerializers.Kryo)
    fun <V: Any> marshalling(): LettuceBinaryCodec<V> = codec(BinarySerializers.Marshalling)
    fun <V: Any> protobuf(): LettuceBinaryCodec<V> = codec(ProtobufSerializer())

    fun <V: Any> gzipJdk(): LettuceBinaryCodec<V> = codec(BinarySerializers.GZipJdk)

    //    fun <V: Any> gzipFst(): LettuceBinaryCodec<V> = codec(BinarySerializers.GZipFst)
    fun <V: Any> gzipKryo(): LettuceBinaryCodec<V> = codec(BinarySerializers.GZipKryo)
    fun <V: Any> gzipMarshalling(): LettuceBinaryCodec<V> = codec(BinarySerializers.GZipMarshalling)
    fun <V: Any> gzipProtobuf(): LettuceBinaryCodec<V> = compressedCodec(Compressors.GZip, protobufSerializer)

    fun <V: Any> deflateJdk(): LettuceBinaryCodec<V> = codec(BinarySerializers.DeflateJdk)

    //    fun <V: Any> deflateFst(): LettuceBinaryCodec<V> = codec(BinarySerializers.DeflateFst)
    fun <V: Any> deflateKryo(): LettuceBinaryCodec<V> = codec(BinarySerializers.DeflateKryo)
    fun <V: Any> deflateMarshalling(): LettuceBinaryCodec<V> = codec(BinarySerializers.DeflateMarshalling)
    fun <V: Any> deflateProtobuf(): LettuceBinaryCodec<V> = compressedCodec(Compressors.Deflate, protobufSerializer)

    fun <V: Any> snappyJdk(): LettuceBinaryCodec<V> = codec(BinarySerializers.SnappyJdk)

    //    fun <V: Any> snappyFst(): LettuceBinaryCodec<V> = codec(BinarySerializers.SnappyFst)
    fun <V: Any> snappyKryo(): LettuceBinaryCodec<V> = codec(BinarySerializers.SnappyKryo)
    fun <V: Any> snappyMarshalling(): LettuceBinaryCodec<V> = codec(BinarySerializers.SnappyMarshalling)
    fun <V: Any> snappyProtobuf(): LettuceBinaryCodec<V> = compressedCodec(Compressors.Snappy, protobufSerializer)

    fun <V: Any> lz4Jdk(): LettuceBinaryCodec<V> = codec(BinarySerializers.LZ4Jdk)

    //    fun <V: Any> lz4Fst(): LettuceBinaryCodec<V> = codec(BinarySerializers.LZ4Fst)
    fun <V: Any> lz4Kryo(): LettuceBinaryCodec<V> = codec(BinarySerializers.LZ4Kryo)
    fun <V: Any> lz4Marshalling(): LettuceBinaryCodec<V> = codec(BinarySerializers.LZ4Marshalling)
    fun <V: Any> lz4Protobuf(): LettuceBinaryCodec<V> = compressedCodec(Compressors.LZ4, protobufSerializer)

    fun <V: Any> zstdJdk(): LettuceBinaryCodec<V> = codec(BinarySerializers.ZstdJdk)

    //    fun <V: Any> zstdFst(): LettuceBinaryCodec<V> = codec(BinarySerializers.ZstdFst)
    fun <V: Any> zstdKryo(): LettuceBinaryCodec<V> = codec(BinarySerializers.ZstdKryo)
    fun <V: Any> zstdMarshalling(): LettuceBinaryCodec<V> = codec(BinarySerializers.ZstdMarshalling)
    fun <V: Any> zstdProtobuf(): LettuceBinaryCodec<V> = compressedCodec(Compressors.Zstd, protobufSerializer)
}
