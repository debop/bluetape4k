package io.bluetape4k.infra.kafka.codec

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

    @Nested
    inner class LZ4JdkKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.LZ4Jdk
    }

    @Nested
    inner class LZ4KryoKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.Lz4Kryo
    }

    @Nested
    inner class SnappyJdkKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.SnappyJdk
    }

    @Nested
    inner class SnappyKryoKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.SnappyKryo
    }

    @Nested
    inner class ZstdJdkKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.ZstdJdk
    }

    @Nested
    inner class ZstdKryoKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any?> = KafkaCodecs.ZstdKryo
    }
}
