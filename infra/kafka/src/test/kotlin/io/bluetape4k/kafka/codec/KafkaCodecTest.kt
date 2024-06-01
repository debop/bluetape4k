package io.bluetape4k.kafka.codec

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested

class KafkaCodecTest {

    @Nested
    inner class JacksonCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.Jackson
    }

    @Nested
    inner class JdkKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.Jdk
    }

    @Nested
    inner class KryoKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.Kryo
    }

    @Disabled("Fury codec이 BigDecimal, BigInteger를 지원하지 않음")
    @Nested
    inner class FuryKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.Fury
    }

    @Nested
    inner class Lz4JdkKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.LZ4Jdk
    }

    @Nested
    inner class Lz4KryoKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.Lz4Kryo
    }

    @Disabled("Fury codec이 BigDecimal, BigInteger를 지원하지 않음")
    @Nested
    inner class Lz4FuryKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.Lz4Fury
    }

    @Nested
    inner class SnappyJdkKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.SnappyJdk
    }

    @Nested
    inner class SnappyKryoKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.SnappyKryo
    }

    @Disabled("Fury codec이 BigDecimal, BigInteger를 지원하지 않음")
    @Nested
    inner class SnappyFuryKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.SnappyFury
    }

    @Nested
    inner class ZstdJdkKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.ZstdJdk
    }

    @Nested
    inner class ZstdKryoKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.ZstdKryo
    }

    @Disabled("Fury codec이 BigDecimal, BigInteger를 지원하지 않음")
    @Nested
    inner class ZstdFuryKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.ZstdFury
    }
}
