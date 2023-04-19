package io.bluetape4k.io.serializer

import io.bluetape4k.io.compressor.Compressors

class BZip2JdkSerializer: CompressableBinarySerializer(BinarySerializers.Jdk, Compressors.BZip2)
class DeflateJdkSerializer: CompressableBinarySerializer(BinarySerializers.Jdk, Compressors.Deflate)
class GZipJdkSerializer: CompressableBinarySerializer(BinarySerializers.Jdk, Compressors.GZip)
class LZ4JdkSerializer: CompressableBinarySerializer(BinarySerializers.Jdk, Compressors.LZ4)
class SnappyJdkSerializer: CompressableBinarySerializer(BinarySerializers.Jdk, Compressors.Snappy)
class ZstdJdkSerializer: CompressableBinarySerializer(BinarySerializers.Jdk, Compressors.Zstd)

//class BZip2FstSerializer : CompressableBinarySerializer(BinarySerializers.Fst, Compressors.BZip2)
//class DeflateFstSerializer : CompressableBinarySerializer(BinarySerializers.Fst, Compressors.Deflate)
//class GZipFstSerializer : CompressableBinarySerializer(BinarySerializers.Fst, Compressors.GZip)
//class LZ4FstSerializer : CompressableBinarySerializer(BinarySerializers.Fst, Compressors.LZ4)
//class SnappyFstSerializer : CompressableBinarySerializer(BinarySerializers.Fst, Compressors.Snappy)
//class ZstdFstSerializer : CompressableBinarySerializer(BinarySerializers.Fst, Compressors.Zstd)

class BZip2KryoSerializer: CompressableBinarySerializer(BinarySerializers.Kryo, Compressors.BZip2)
class DeflateKryoSerializer: CompressableBinarySerializer(BinarySerializers.Kryo, Compressors.Deflate)
class GZipKryoSerializer: CompressableBinarySerializer(BinarySerializers.Kryo, Compressors.GZip)
class LZ4KryoSerializer: CompressableBinarySerializer(BinarySerializers.Kryo, Compressors.LZ4)
class SnappyKryoSerializer: CompressableBinarySerializer(BinarySerializers.Kryo, Compressors.Snappy)
class ZstdKryoSerializer: CompressableBinarySerializer(BinarySerializers.Kryo, Compressors.Zstd)

class BZip2MarshallingSerializer: CompressableBinarySerializer(BinarySerializers.Marshalling, Compressors.BZip2)
class DeflateMarshallingSerializer: CompressableBinarySerializer(BinarySerializers.Marshalling, Compressors.Deflate)
class GZipMarshallingSerializer: CompressableBinarySerializer(BinarySerializers.Marshalling, Compressors.GZip)
class LZ4MarshallingSerializer: CompressableBinarySerializer(BinarySerializers.Marshalling, Compressors.LZ4)
class SnappyMarshallingSerializer: CompressableBinarySerializer(BinarySerializers.Marshalling, Compressors.Snappy)
class ZstdMarshallingSerializer: CompressableBinarySerializer(BinarySerializers.Marshalling, Compressors.Zstd)
