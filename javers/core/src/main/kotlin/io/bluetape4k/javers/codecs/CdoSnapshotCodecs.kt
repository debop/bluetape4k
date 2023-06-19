package io.bluetape4k.javers.codecs

import io.bluetape4k.io.serializer.BinarySerializers

object CdoSnapshotCodecs {

    val Default by lazy { LZ4Kryo }

    val Kryo by lazy { BinaryCdoSnapshotCodec(BinarySerializers.Kryo) }
    val LZ4Kryo by lazy { BinaryCdoSnapshotCodec(BinarySerializers.LZ4Kryo) }
    val SnappyKryo by lazy { BinaryCdoSnapshotCodec(BinarySerializers.SnappyKryo) }
    val ZstdKryo by lazy { BinaryCdoSnapshotCodec(BinarySerializers.ZstdKryo) }

}
