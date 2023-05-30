package io.bluetape4k.data.redis.spring.serializer

import io.bluetape4k.io.serializer.BinarySerializer
import io.bluetape4k.logging.KLogging
import org.springframework.data.redis.serializer.RedisSerializer

class RedisBinarySerializer private constructor(
    private val bs: BinarySerializer,
): RedisSerializer<Any> {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(bs: BinarySerializer): RedisBinarySerializer {
            return RedisBinarySerializer(bs)
        }
    }

    override fun serialize(t: Any?): ByteArray? {
        return bs.serialize(t)
    }

    override fun deserialize(bytes: ByteArray?): Any? {
        return bs.deserialize(bytes)
    }
}
