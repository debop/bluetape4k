package io.bluetape4k.data.redis.redisson.codec

import com.google.protobuf.Message
import io.bluetape4k.io.netty.buffer.getBytes
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.warn
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.Unpooled
import org.redisson.client.codec.BaseCodec
import org.redisson.client.codec.Codec
import org.redisson.client.handler.State
import org.redisson.client.protocol.Decoder
import org.redisson.client.protocol.Encoder
import org.redisson.codec.Kryo5Codec
import java.util.concurrent.ConcurrentHashMap


typealias AnyMessage = com.google.protobuf.Any

/**
 * Proto Buffer 객체를 Redisson 에서 사용하기 위한 Codec
 *
 * @property fallbackCodec 내부 코덱
 * @constructor Create empty Protobuf codec
 */
class ProtobufCodec private constructor(private val fallbackCodec: Codec): BaseCodec() {

    companion object: KLogging() {

        @JvmStatic
        val INSTANCE: ProtobufCodec by lazy { ProtobufCodec() }

        private val FALLBACK_CODEC by lazy { Kryo5Codec() }

        private val emptyByteArray = ByteArray(0)

        @JvmStatic
        operator fun invoke(fallbackCodec: BaseCodec = FALLBACK_CODEC): ProtobufCodec {
            return ProtobufCodec(fallbackCodec)
        }

        // classLoader를 인자로 받는 보조 생성자는 Redisson에서 환경설정 정보를 바탕으로 동적으로 Codec 생성 시에 필요합니다.
        @JvmStatic
        operator fun invoke(classLoader: ClassLoader): ProtobufCodec {
            return invoke()
        }

        @JvmStatic
        operator fun invoke(classLoader: ClassLoader, codec: ProtobufCodec): ProtobufCodec {
            return invoke(classLoader)
        }

        val classCache = ConcurrentHashMap<String, Class<Message>>()
    }

    // private val fallbackCodec by lazy { MarshallingCodec() }

    private val _encoder: Encoder = Encoder { graph ->
        if (graph is Message) {
            val bytes = AnyMessage.pack(graph).toByteArray()
            val out = ByteBufAllocator.DEFAULT.buffer(bytes.size)
            out.writeBytes(bytes)
        } else {
            log.warn { "Value is not Protobuf Message instance. graph class=${graph.javaClass}" }
            fallbackCodec.valueEncoder.encode(graph)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val _decoder: Decoder<Any> = Decoder<Any> { buf: ByteBuf, state: State ->
        try {
            val bytes = buf.getBytes(copy = false)
            val any = AnyMessage.parseFrom(bytes)
            val className = any.typeUrl.substringAfterLast("/")
            val clazz = classCache.computeIfAbsent(className) {
                Class.forName(it) as Class<Message>
            }
            any.unpack(clazz)
        } catch (e: Throwable) {
            log.warn { "Fail to decode as Protobuf message. it is not Protobuf Message." }
            fallbackCodec.valueDecoder.decode(Unpooled.wrappedBuffer(buf.resetReaderIndex()), state)
        }
    }

    override fun getValueEncoder(): Encoder = _encoder

    override fun getValueDecoder(): Decoder<Any> = _decoder
}
