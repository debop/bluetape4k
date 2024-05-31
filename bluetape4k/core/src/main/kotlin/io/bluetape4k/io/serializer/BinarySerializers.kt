package io.bluetape4k.io.serializer

import io.bluetape4k.support.unsafeLazy


/**
 * BinarySerializers
 */
object BinarySerializers {
    /** Default Serializer */
    val Default: BinarySerializer by unsafeLazy { Jdk }

    val Jdk: JdkSerializer by unsafeLazy { JdkSerializer() }

    val Kryo: KryoSerializer by unsafeLazy { KryoSerializer() }
    val Marshalling: MarshallingSerializer by unsafeLazy { MarshallingSerializer() }
    val Fury: FurySerializer by unsafeLazy { FurySerializer() }

    val BZip2Jdk: BZip2JdkSerializer by unsafeLazy { BZip2JdkSerializer() }
    val DeflateJdk: DeflateJdkSerializer by unsafeLazy { DeflateJdkSerializer() }
    val GZipJdk: GZipJdkSerializer by unsafeLazy { GZipJdkSerializer() }
    val LZ4Jdk: LZ4JdkSerializer by unsafeLazy { LZ4JdkSerializer() }
    val SnappyJdk: SnappyJdkSerializer by unsafeLazy { SnappyJdkSerializer() }
    val ZstdJdk: BinarySerializer by unsafeLazy { ZstdJdkSerializer() }

    val BZip2Kryo: BZip2KryoSerializer by unsafeLazy { BZip2KryoSerializer() }
    val DeflateKryo: DeflateKryoSerializer by unsafeLazy { DeflateKryoSerializer() }
    val GZipKryo: GZipKryoSerializer by unsafeLazy { GZipKryoSerializer() }
    val LZ4Kryo: LZ4KryoSerializer by unsafeLazy { LZ4KryoSerializer() }
    val SnappyKryo: SnappyKryoSerializer by unsafeLazy { SnappyKryoSerializer() }
    val ZstdKryo: BinarySerializer by unsafeLazy { ZstdKryoSerializer() }

    val BZip2Marshalling: BZip2MarshallingSerializer by unsafeLazy { BZip2MarshallingSerializer() }
    val DeflateMarshalling: DeflateMarshallingSerializer by unsafeLazy { DeflateMarshallingSerializer() }
    val GZipMarshalling: GZipMarshallingSerializer by unsafeLazy { GZipMarshallingSerializer() }
    val LZ4Marshalling: LZ4MarshallingSerializer by unsafeLazy { LZ4MarshallingSerializer() }
    val SnappyMarshalling: SnappyMarshallingSerializer by unsafeLazy { SnappyMarshallingSerializer() }
    val ZstdMarshalling: BinarySerializer by unsafeLazy { ZstdMarshallingSerializer() }

    val BZip2Fury: BZip2FurySerializer by unsafeLazy { BZip2FurySerializer() }
    val DeflateFury: DeflateFurySerializer by unsafeLazy { DeflateFurySerializer() }
    val GZipFury: GZipFurySerializer by unsafeLazy { GZipFurySerializer() }
    val LZ4Fury: LZ4FurySerializer by unsafeLazy { LZ4FurySerializer() }
    val SnappyFury: SnappyFurySerializer by unsafeLazy { SnappyFurySerializer() }
    val ZstdFury: BinarySerializer by unsafeLazy { ZstdFurySerializer() }
}
