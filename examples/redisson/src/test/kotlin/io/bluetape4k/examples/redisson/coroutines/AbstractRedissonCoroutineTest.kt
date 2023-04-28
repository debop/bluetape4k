package io.bluetape4k.examples.redisson.coroutines

import io.bluetape4k.data.redis.redisson.redissonClientOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import io.bluetape4k.testcontainers.storage.RedisServer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.datafaker.Faker
import org.redisson.api.RedissonClient

abstract class AbstractRedissonCoroutineTest {

    companion object: KLogging() {

        @JvmStatic
        val redis: RedisServer by lazy { RedisServer.Launcher.redis }

        @JvmStatic
        val redissonClient by lazy {
            RedisServer.Launcher.RedissonLib.getRedisson()
        }

        @JvmStatic
        val faker = Faker()

        @JvmStatic
        protected fun randomName(): String =
            "bluetape4k:${faker.name().username()}:${faker.random().nextLong(100, 99999)}"

    }

    protected val redisson: RedissonClient get() = redissonClient

    protected fun newRedisson(): RedissonClient {
        val config = RedisServer.Launcher.RedissonLib.getRedissonConfig()
        return redissonClientOf(config)
    }

    protected val scope = CoroutineScope(CoroutineName("redisson") + Dispatchers.IO)

    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        log.error(exception) {
            "CoroutineExceptionHandler get $exception with suppressed ${exception.suppressed.contentToString()} "
        }
        throw RuntimeException("Fail to execute in coroutine", exception)
    }
}