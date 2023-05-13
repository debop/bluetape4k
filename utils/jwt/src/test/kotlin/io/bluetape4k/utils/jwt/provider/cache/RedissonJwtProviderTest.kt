package io.bluetape4k.utils.jwt.provider.cache

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer
import io.bluetape4k.utils.jwt.provider.AbstractJwtProviderTest
import io.bluetape4k.utils.jwt.provider.JwtProvider
import io.bluetape4k.utils.jwt.provider.JwtProviderFactory
import io.bluetape4k.utils.jwt.reader.JwtReaderDto
import org.redisson.codec.LZ4Codec

class RedissonJwtProviderTest: AbstractJwtProviderTest() {

    companion object: KLogging()

    private val redissonClient by lazy {
        RedisServer.Launcher.RedissonLib.getRedisson()
    }

    private val readerCache by lazy {
        redissonClient.getMapCache<String, JwtReaderDto>("bluetapepe4k:jwt:reaer", LZ4Codec())
    }

    private val delegate = JwtProviderFactory.default(keyChainRepository = repository)

    override val provider: JwtProvider = JwtProviderFactory.redissonCached(delegate, readerCache)
}
