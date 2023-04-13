package io.bluetape4k.io.serializer

import io.bluetape4k.io.compressor.Compressors

class BZip2JdkSerializer : CompressableBinarySerializer(Serializers.Jdk, Compressors.BZip2)
class DeflateJdkSerializer : CompressableBinarySerializer(Serializers.Jdk, Compressors.Deflate)
class GZipJdkSerializer : CompressableBinarySerializer(Serializers.Jdk, Compressors.GZip)
class LZ4JdkSerializer : CompressableBinarySerializer(Serializers.Jdk, Compressors.LZ4)
class SnappyJdkSerializer : CompressableBinarySerializer(Serializers.Jdk, Compressors.Snappy)
class ZstdJdkSerializer : CompressableBinarySerializer(Serializers.Jdk, Compressors.Zstd)

class BZip2FstSerializer : CompressableBinarySerializer(Serializers.Fst, Compressors.BZip2)
class DeflateFstSerializer : CompressableBinarySerializer(Serializers.Fst, Compressors.Deflate)
class GZipFstSerializer : CompressableBinarySerializer(Serializers.Fst, Compressors.GZip)
class LZ4FstSerializer : CompressableBinarySerializer(Serializers.Fst, Compressors.LZ4)
class SnappyFstSerializer : CompressableBinarySerializer(Serializers.Fst, Compressors.Snappy)
class ZstdFstSerializer : CompressableBinarySerializer(Serializers.Fst, Compressors.Zstd)

class BZip2KryoSerializer : CompressableBinarySerializer(Serializers.Kryo, Compressors.BZip2)
class DeflateKryoSerializer : CompressableBinarySerializer(Serializers.Kryo, Compressors.Deflate)
class GZipKryoSerializer : CompressableBinarySerializer(Serializers.Kryo, Compressors.GZip)
class LZ4KryoSerializer : CompressableBinarySerializer(Serializers.Kryo, Compressors.LZ4)
class SnappyKryoSerializer : CompressableBinarySerializer(Serializers.Kryo, Compressors.Snappy)
class ZstdKryoSerializer : CompressableBinarySerializer(Serializers.Kryo, Compressors.Zstd)

class BZip2MarshallingSerializer : CompressableBinarySerializer(Serializers.Marshalling, Compressors.BZip2)
class DeflateMarshallingSerializer : CompressableBinarySerializer(Serializers.Marshalling, Compressors.Deflate)
class GZipMarshallingSerializer : CompressableBinarySerializer(Serializers.Marshalling, Compressors.GZip)
class LZ4MarshallingSerializer : CompressableBinarySerializer(Serializers.Marshalling, Compressors.LZ4)
class SnappyMarshallingSerializer : CompressableBinarySerializer(Serializers.Marshalling, Compressors.Snappy)
class ZstdMarshallingSerializer : CompressableBinarySerializer(Serializers.Marshalling, Compressors.Zstd)
