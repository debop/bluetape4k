package io.bluetape4k.data.redis

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer

abstract class AbstractRedisTest {

    companion object: KLogging() {

        @JvmStatic
        val redis: RedisServer by lazy { RedisServer.Launcher.redis }

        @JvmStatic
        val faker = Fakers.faker

        @JvmStatic
        protected fun randomName(): String =
            "bluetape4k:${faker.name().username()}:${faker.random().nextLong(100, 99999)}"

        @JvmStatic
        protected fun randomString(): String =
            Fakers.randomString(1024, 4096, true)

    }
}
