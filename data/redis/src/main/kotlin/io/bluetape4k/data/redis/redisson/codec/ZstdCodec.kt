package io.bluetape4k.data.redis.redisson.codec

import io.bluetape4k.io.compressor.Compressors
import io.bluetape4k.logging.KLogging
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import org.redisson.client.codec.BaseCodec
import org.redisson.client.codec.Codec
import org.redisson.client.handler.State
import org.redisson.client.protocol.Decoder
import org.redisson.client.protocol.Encoder
import org.redisson.codec.Kryo5Codec

class ZstdCodec private constructor(private val innerCodec: Codec): BaseCodec() {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(innerCodec: Codec = Kryo5Codec()): ZstdCodec {
            return ZstdCodec(innerCodec)
        }

        @JvmStatic
        operator fun invoke(classLoader: ClassLoader): ZstdCodec {
            return invoke(Kryo5Codec(classLoader))
        }

        @JvmStatic
        operator fun invoke(classLoader: ClassLoader, codec: ZstdCodec) {
            invoke(copy(classLoader, codec.innerCodec))
        }
    }

    private val zstd = Compressors.Zstd

    private val _encoder: Encoder = Encoder { graph ->
        val encoded = innerCodec.valueEncoder.encode(graph)

        val bytes = ByteBufUtil.getBytes(encoded, encoded.readerIndex(), encoded.readableBytes(), true)
        encoded.release()
        val res = zstd.compress(bytes)
        Unpooled.wrappedBuffer(res)
    }

    private val _decoder: Decoder<Any> = Decoder<Any> { buf: ByteBuf, state: State ->
        val bytes = ByteBufUtil.getBytes(buf, buf.readerIndex(), buf.readableBytes(), true)
        val plainBytes = zstd.decompress(bytes)
        val decoded = Unpooled.wrappedBuffer(plainBytes)

        try {
            innerCodec.valueDecoder.decode(decoded, state)
        } finally {
            decoded.release()
        }
    }


    override fun getValueEncoder(): Encoder = _encoder
    override fun getValueDecoder(): Decoder<Any> = _decoder
}
