package io.bluetape4k.redis.spring.serializer

import io.bluetape4k.io.compressor.Compressor
import io.bluetape4k.io.compressor.Compressors
import io.bluetape4k.logging.KLogging
import org.springframework.data.redis.serializer.RedisSerializer

class RedisCompressSerializer private constructor(
    private val compressor: Compressor,
): RedisSerializer<ByteArray> {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(compressor: Compressor = Compressors.LZ4): RedisCompressSerializer {
            return RedisCompressSerializer(compressor)
        }
    }

    override fun serialize(t: ByteArray?): ByteArray? {
        return compressor.compress(t)
    }

    override fun deserialize(bytes: ByteArray?): ByteArray? {
        return compressor.decompress(bytes)
    }
}
