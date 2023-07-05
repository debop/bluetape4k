package io.bluetape4k.infra.kafka.codec

object KafkaCodecs {

    val String by lazy { StringKafkaCodec() }
    val ByteArray by lazy { ByteArrayKafkaCodec() }

    val Jackson by lazy { JacksonKafkaCodec() }

    val Jdk by lazy { JdkKafkaCodec() }
    val Kryo by lazy { KryoKafkaCodec() }

    val LZ4Jdk by lazy { LZ4JdkKafkaCodec() }
    val Lz4Kryo by lazy { LZ4KryoKafkaCodec() }

    val SnappyJdk by lazy { SnappyJdkKafkaCodec() }
    val SnappyKryo by lazy { SnappyKryoKafkaCodec() }

    val ZstdJdk by lazy { ZstdJdkKafkaCodec() }
    val ZstdKryo by lazy { ZstdKryoKafkaCodec() }
}
