package io.bluetape4k.data.redis

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer
import net.datafaker.Faker

abstract class AbstractRedisTest {

    companion object: KLogging() {

        @JvmStatic
        val redis: RedisServer by lazy { RedisServer.Launcher.redis }

        @JvmStatic
        val faker = Faker()

        @JvmStatic
        protected fun randomName(): String =
            "bluetape4k:${faker.name().username()}:${faker.random().nextLong(100, 99999)}"
        // Fakers.randomString(32, 256, true)
    }
}
