package io.bluetape4k.io.serializer

import io.bluetape4k.io.compressor.Compressors


/**
 * BinarySerializers
 */
object BinarySerializers {
    /** Default Serializer */
    val Default: BinarySerializer by lazy { Jdk }

    val Jdk: JdkSerializer by lazy { JdkSerializer() }

    val Kryo: KryoSerializer by lazy { KryoSerializer() }
    val Marshalling: MarshallingSerializer by lazy { MarshallingSerializer() }

    val BZip2Jdk: BZip2JdkSerializer by lazy { BZip2JdkSerializer() }
    val DeflateJdk: DeflateJdkSerializer by lazy { DeflateJdkSerializer() }
    val GZipJdk: GZipJdkSerializer by lazy { GZipJdkSerializer() }
    val LZ4Jdk: LZ4JdkSerializer by lazy { LZ4JdkSerializer() }
    val SnappyJdk: SnappyJdkSerializer by lazy { SnappyJdkSerializer() }
    val ZstdJdk: BinarySerializer by lazy { CompressableBinarySerializer(BinarySerializers.Jdk, Compressors.Zstd) }

    val BZip2Kryo: BZip2KryoSerializer by lazy { BZip2KryoSerializer() }
    val DeflateKryo: DeflateKryoSerializer by lazy { DeflateKryoSerializer() }
    val GZipKryo: GZipKryoSerializer by lazy { GZipKryoSerializer() }
    val LZ4Kryo: LZ4KryoSerializer by lazy { LZ4KryoSerializer() }
    val SnappyKryo: SnappyKryoSerializer by lazy { SnappyKryoSerializer() }
    val ZstdKryo: BinarySerializer by lazy { CompressableBinarySerializer(BinarySerializers.Kryo, Compressors.Zstd) }

    val BZip2Marshalling: BZip2MarshallingSerializer by lazy { BZip2MarshallingSerializer() }
    val DeflateMarshalling: DeflateMarshallingSerializer by lazy { DeflateMarshallingSerializer() }
    val GZipMarshalling: GZipMarshallingSerializer by lazy { GZipMarshallingSerializer() }
    val LZ4Marshalling: LZ4MarshallingSerializer by lazy { LZ4MarshallingSerializer() }
    val SnappyMarshalling: SnappyMarshallingSerializer by lazy { SnappyMarshallingSerializer() }
    val ZstdMarshalling: BinarySerializer by lazy {
        CompressableBinarySerializer(BinarySerializers.Marshalling, Compressors.Zstd)
    }
}