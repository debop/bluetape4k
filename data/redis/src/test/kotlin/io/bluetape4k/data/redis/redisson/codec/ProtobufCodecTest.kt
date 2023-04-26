package io.bluetape4k.data.redis.redisson.codec

import com.google.protobuf.timestamp
import io.bluetape4k.data.redis.messages.*
import io.bluetape4k.data.redis.redisson.AbstractRedissonTest
import io.bluetape4k.data.redis.redisson.RedissonCodecs
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.redisson.client.codec.Codec
import org.redisson.client.handler.State
import java.io.Serializable
import java.time.Instant

class ProtobufCodecTest : AbstractRedissonTest() {

    companion object : KLogging() {
        private const val REPEAT_SIZE = 10
    }

    private val protobufCodec = RedissonCodecs.Protobuf
    private val lz4ProtobufCodec = RedissonCodecs.LZ4Protobuf

    private fun getTestCodecs() = listOf(
        Arguments.of(protobufCodec),
        Arguments.of(lz4ProtobufCodec)
    )

    data class CustomData(
        val id: Int,
        val name: String
    ) : Serializable

    private fun Instant.toProtobufTimestamp(): com.google.protobuf.Timestamp {
        val source = this
        return timestamp {
            this.seconds = source.epochSecond
            this.nanos = source.nano
        }
    }

    private fun newSimpleMessage(): SimpleMessage = simpleMessage {
        id = faker.random().nextLong()
        name = faker.name().fullName()
        description = Fakers.randomString(1024, 4096, true)
        timestamp = Instant.now().toProtobufTimestamp()
    }

    private fun newNestedMessage(): NestedMessage = nestedMessage {
        id = faker.random().nextLong()
        name = faker.name().fullName()
        dayOfTheWeek = DayOfTheWeek.FRIDAY
        optionalMessage = newSimpleMessage()
        nestedMessages.add(newSimpleMessage().copy { id = faker.random().nextLong() })
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> Codec.verifyCodec(origin: T) {
        val buf = valueEncoder.encode(origin)
        val actual = valueDecoder.decode(buf, State()) as? T
        actual shouldBeEqualTo origin
    }

    @ParameterizedTest(name = "codec for simple string with {0}")
    @MethodSource("getTestCodecs")
    fun `codec for simple string`(codec: Codec) {
        val origin = "Hello world! 동해물과 백두산이"
        codec.verifyCodec(origin)
    }

    @ParameterizedTest(name = "codec for kotlin data class with {0}")
    @MethodSource("getTestCodecs")
    fun `codec for kotlin data class`(codec: Codec) {
        repeat(REPEAT_SIZE) {
            val origin = CustomData(faker.random().nextInt(), faker.name().fullName())
            codec.verifyCodec(origin)
        }
    }

    @ParameterizedTest(name = "codec for protobuf simple message with {0}")
    @MethodSource("getTestCodecs")
    fun `codec for protobuf simple message`(codec: Codec) {
        repeat(REPEAT_SIZE) {
            codec.verifyCodec(newSimpleMessage())
        }
    }

    @ParameterizedTest(name = "codec for protobuf nested message with {0}")
    @MethodSource("getTestCodecs")
    fun `codec for protobuf nested message`(codec: Codec) {
        repeat(REPEAT_SIZE) {
            codec.verifyCodec(newNestedMessage())
        }
    }
}
