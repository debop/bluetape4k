package io.bluetape4k.infra.kafka.codec

import org.junit.jupiter.api.Nested

class KafkaCodecTest {

    @Nested
    inner class JacksonCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any> = JacksonKafkaCodec()
    }

    @Nested
    inner class JdkKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any> = JdkKafkaCodec()
    }

    @Nested
    inner class Kryo5KafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any> = Kryo5KafkaCodec()
    }

    @Nested
    inner class LZ4JdkKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any> = LZ4JdkKafkaCodec()
    }

    @Nested
    inner class LZ4Kryo5KafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any> = LZ4Kryo5KafkaCodec()
    }

    @Nested
    inner class SnappyJdkKafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any> = SnappyJdkKafkaCodec()
    }

    @Nested
    inner class SnappyKryo5KafkaCodecTest: AbstractKafkaCodecTest() {
        override val codec: KafkaCodec<Any> = SnappyKryo5KafkaCodec()
    }
}
