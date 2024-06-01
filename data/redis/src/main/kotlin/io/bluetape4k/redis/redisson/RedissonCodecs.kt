package io.bluetape4k.redis.redisson

import io.bluetape4k.logging.KLogging
import io.bluetape4k.redis.redisson.codec.FuryCodec
import io.bluetape4k.redis.redisson.codec.GzipCodec
import io.bluetape4k.redis.redisson.codec.Lz4Codec
import io.bluetape4k.redis.redisson.codec.ProtobufCodec
import io.bluetape4k.redis.redisson.codec.ZstdCodec
import io.bluetape4k.support.unsafeLazy
import org.redisson.client.codec.Codec
import org.redisson.client.codec.DoubleCodec
import org.redisson.client.codec.IntegerCodec
import org.redisson.client.codec.LongCodec
import org.redisson.client.codec.StringCodec
import org.redisson.codec.CompositeCodec
import org.redisson.codec.Kryo5Codec
import org.redisson.codec.SerializationCodec
import org.redisson.codec.SnappyCodecV2

/**
 * Redisson 자체가 Java 21 기준이 아니므로,
 * Kommons 에서 제공하는 FuryCodec, ProtobufCodec 을 Redisson 에서 단독으로 사용하는 것은 가능하지만, Wrapping 해서 사용하지는 못한다.
 * 즉 Kryo5Codec(FuryCodec()) 은 인식하지 못한다.
 *
 * 그래서 Kommons에서 제공하는 FuryCodec, ProtobufCodec 을 사용하려면, ZstdCodec, GzipCodec 와 결합하여 사용하고, LZ, Snappy 와는 사용하면 안된다.
 *
 *
 * @constructor Create empty Redisson codecs
 */
object RedissonCodecs: KLogging() {

    /**
     * Redisson 의 기본 Codec (Kryo5) 입니다.
     */
    @JvmStatic
    val Default: Codec by unsafeLazy { RedissonCodecs.Kryo5 }

    val Int: Codec by unsafeLazy { IntegerCodec() }
    val Long: Codec by unsafeLazy { LongCodec() }
    val Double: Codec by unsafeLazy { DoubleCodec() }
    val String: Codec by unsafeLazy { StringCodec() }

    val Kryo5: Codec by unsafeLazy { Kryo5Codec() }
    val Protobuf: Codec by unsafeLazy { ProtobufCodec() }

    /**
     * 최고 성능을 발휘하는 Codec 입니다.
     */
    val Fury: Codec by unsafeLazy { FuryCodec() }
    val Jdk: Codec by unsafeLazy { SerializationCodec() }

    val Kryo5Composite: Codec by unsafeLazy { CompositeCodec(String, Kryo5, Kryo5) }
    val ProtobufComposite: Codec by unsafeLazy { CompositeCodec(String, Protobuf, Protobuf) }
    val FuryComposite: Codec by unsafeLazy { CompositeCodec(String, Fury, Fury) }
    val JdkComposite: Codec by unsafeLazy { CompositeCodec(String, Jdk, Jdk) }

    val SnappyKryo5: Codec by unsafeLazy { SnappyCodecV2(Kryo5) }
    val SnappyProtobuf: Codec by unsafeLazy { SnappyCodecV2(Protobuf) }
    val SnappyFury: Codec by unsafeLazy { SnappyCodecV2(Fury) }
    val SnappyJdk: Codec by unsafeLazy { SnappyCodecV2(Jdk) }

    val SnappyKryo5Composite: Codec by unsafeLazy { CompositeCodec(String, SnappyKryo5, SnappyKryo5) }
    val SnappyProtobufComposite: Codec by unsafeLazy { CompositeCodec(String, SnappyProtobuf, SnappyProtobuf) }
    val SnappyFuryComposite: Codec by unsafeLazy { CompositeCodec(String, SnappyFury, SnappyFury) }
    val SnappyJdkComposite: Codec by unsafeLazy { CompositeCodec(String, SnappyJdk, SnappyJdk) }

    val LZ4Kryo5: Codec by unsafeLazy { Lz4Codec(Kryo5) }
    val LZ4Protobuf: Codec by unsafeLazy { Lz4Codec(Protobuf) }
    val LZ4Fury: Codec by unsafeLazy { Lz4Codec(Fury) }
    val LZ4Jdk: Codec by unsafeLazy { Lz4Codec(Jdk) }

    val LZ4Kryo5Composite: Codec by unsafeLazy { CompositeCodec(String, LZ4Kryo5, LZ4Kryo5) }
    val LZ4ProtobufComposite: Codec by unsafeLazy { CompositeCodec(String, LZ4Protobuf, LZ4Protobuf) }
    val LZ4FuryComposite: Codec by unsafeLazy { CompositeCodec(String, LZ4Fury, LZ4Fury) }
    val LZ4JdkComposite: Codec by unsafeLazy { CompositeCodec(String, LZ4Jdk, LZ4Jdk) }

    val ZstdKryo5: Codec by unsafeLazy { ZstdCodec(Kryo5) }
    val ZstdProtobuf: Codec by unsafeLazy { ZstdCodec(Protobuf) }
    val ZstdFury: Codec by unsafeLazy { ZstdCodec(Fury) }
    val ZstdJdk: Codec by unsafeLazy { ZstdCodec(Jdk) }

    val ZstdKryo5Composite: Codec by unsafeLazy { CompositeCodec(String, ZstdKryo5, ZstdKryo5) }
    val ZstdProtobufComposite: Codec by unsafeLazy { CompositeCodec(String, ZstdProtobuf, ZstdProtobuf) }
    val ZstdFuryComposite: Codec by unsafeLazy { CompositeCodec(String, ZstdFury, ZstdFury) }
    val ZstdJdkComposite: Codec by unsafeLazy { CompositeCodec(String, ZstdJdk, ZstdJdk) }

    val GzipKryo5: Codec by unsafeLazy { GzipCodec(Kryo5) }
    val GzipProtobuf: Codec by unsafeLazy { GzipCodec(Protobuf) }
    val GzipFury: Codec by unsafeLazy { GzipCodec(Fury) }
    val GzipJdk: Codec by unsafeLazy { GzipCodec(Jdk) }

    val GzipKryo5Composite: Codec by unsafeLazy { CompositeCodec(String, GzipKryo5, GzipKryo5) }
    val GzipProtobufComposite: Codec by unsafeLazy { CompositeCodec(String, GzipProtobuf, GzipProtobuf) }
    val GzipFuryComposite: Codec by unsafeLazy { CompositeCodec(String, GzipFury, GzipFury) }
    val GzipJdkComposite: Codec by unsafeLazy { CompositeCodec(String, GzipJdk, GzipJdk) }
}
