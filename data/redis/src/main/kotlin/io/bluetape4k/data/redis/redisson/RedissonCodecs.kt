package io.bluetape4k.data.redis.redisson

import io.bluetape4k.data.redis.redisson.codec.ProtobufCodec
import io.bluetape4k.data.redis.redisson.codec.ZstdCodec
import io.bluetape4k.logging.KLogging
import org.redisson.client.codec.Codec
import org.redisson.client.codec.DoubleCodec
import org.redisson.client.codec.IntegerCodec
import org.redisson.client.codec.LongCodec
import org.redisson.client.codec.StringCodec
import org.redisson.codec.CompositeCodec
import org.redisson.codec.Kryo5Codec
import org.redisson.codec.KryoCodec
import org.redisson.codec.LZ4Codec
import org.redisson.codec.SnappyCodecV2

object RedissonCodecs: KLogging() {

    @JvmStatic
    val Default: Codec by lazy { RedissonCodecs.Kryo5 }

    val Int: Codec by lazy { IntegerCodec() }
    val Long: Codec by lazy { LongCodec() }
    val Double: Codec by lazy { DoubleCodec() }
    val String: Codec by lazy { StringCodec() }

    val Kryo: Codec by lazy { KryoCodec() }
    val Kryo5: Codec by lazy { Kryo5Codec() }
    val Protobuf: Codec by lazy { ProtobufCodec() }

    val KryoComposite: Codec by lazy { CompositeCodec(String, Kryo, Kryo) }
    val Kryo5Composite: Codec by lazy { CompositeCodec(String, Kryo5, Kryo5) }
    val ProtobufComposite: Codec by lazy { CompositeCodec(String, Protobuf, Protobuf) }

    val SnappyKryo: Codec by lazy { SnappyCodecV2(Kryo) }
    val SnappyKryo5: Codec by lazy { SnappyCodecV2(Kryo5) }
    val SnappyProtobuf: Codec by lazy { SnappyCodecV2(Protobuf) }

    val SnappyKryoComposite: Codec by lazy { CompositeCodec(String, SnappyKryo, SnappyKryo) }
    val SnappyKryo5Composite: Codec by lazy { CompositeCodec(String, SnappyKryo5, SnappyKryo5) }
    val SnappyProtobufComposite: Codec by lazy { CompositeCodec(String, SnappyProtobuf, SnappyProtobuf) }

    val LZ4Kryo: Codec by lazy { LZ4Codec(Kryo) }
    val LZ4Kryo5: Codec by lazy { LZ4Codec(Kryo5) }
    val LZ4Protobuf: Codec by lazy { LZ4Codec(Protobuf) }

    val LZ4KryoComposite: Codec by lazy { CompositeCodec(String, LZ4Kryo, LZ4Kryo) }
    val LZ4Kryo5Composite: Codec by lazy { CompositeCodec(String, LZ4Kryo5, LZ4Kryo5) }
    val LZ4ProtobufComposite: Codec by lazy { CompositeCodec(String, LZ4Protobuf, LZ4Protobuf) }

    val ZstdKryo: Codec by lazy { ZstdCodec(Kryo) }
    val ZstdKryo5: Codec by lazy { ZstdCodec(Kryo5) }
    val ZstdProtobuf: Codec by lazy { ZstdCodec(Protobuf) }

    val ZstdKryoComposite: Codec by lazy { CompositeCodec(String, ZstdKryo, ZstdKryo) }
    val ZstdKryo5Composite: Codec by lazy { CompositeCodec(String, ZstdKryo5, ZstdKryo5) }
    val ZstdProtobufComposite: Codec by lazy { CompositeCodec(String, ZstdProtobuf, ZstdProtobuf) }
}
